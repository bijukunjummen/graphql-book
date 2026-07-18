# Transactional Outbox Pattern - Part 2

In [Part 1](https://bijukunjummen.medium.com/transactional-outbox-pattern-1-e5b6378ff6c9), I showed why in-process listeners are not enough for reliable event delivery: one flaky listener silently dropped events, and there was no recovery path.

In this post, we move event transport to a message broker (SQS) and show two things:

1. A broker improves reliability for downstream processing because failed consumers can retry.
2. A broker alone is still not enough because producer-side failures can prevent messages from reaching SQS at all.

That second point is exactly where transactional outbox comes in.

## Why SQS Helps

When a consumer fails temporarily, a broker can redeliver the message. This is a huge improvement over in-memory event dispatch where a failed listener just loses the event.

In this repo, the write path now publishes domain events through an outbox publisher abstraction:

- `src/main/java/org/bk/books/service/book/BookServiceImpl.java`
- `src/main/java/org/bk/books/components/outbox/OutboxMessagePublisher.java`
- `src/main/java/org/bk/books/components/outbox/SqsOutboxMessagePublisher.java`

The publish step is now:

```java
outboxMessagePublisher.publish(
    new BookCreatedEvent(uuids.generateUuid(), newBook.id(), newBook.name(), newBook.pageCount(), newBook.authors()));
```

The SQS publisher serializes the event into an envelope and sends to queue `outboxqueue`:

```java
outboxSqsTemplate.send(
    sqsOutboxQueueName,
    new Envelope(objectMapper.writeValueAsString(message), message.getClass().getName()));
```

## SQS Listener and Flaky Consumer

Incoming SQS messages are consumed here:

- `src/main/java/org/bk/books/components/outbox/SqsOutboxMessageListener.java`

```java
@SqsListener("outboxqueue")
public void listen(Envelope message) {
    Object event = objectMapper.readValue(message.event(), Class.forName(message.eventType()));
    eventDispatcher.dispatch(event);
}
```

From there, events are dispatched to Spring event listeners. For `BookCreatedEvent`, one listener is intentionally flaky:

- `src/main/java/org/bk/books/listeners/BookEventListeners.java`

```java
@EventListener
public void bookCreatedListener2(BookCreatedEvent bookCreatedEvent) {
    int value = ThreadLocalRandom.current().nextInt(100);
    if (value < 50) {
        throw new RuntimeException("Could not process bookCreatedEvent");
    }
    LOG.info("Successfully processed ...");
}
```

This simulates a realistic downstream transient failure. Because SQS owns delivery, the same message can be retried and eventually succeed.

## Run It Locally

Start dependencies:

```bash
docker compose up -d
```

Create queue (example using AWS CLI pointed at local endpoint):

```bash
aws --endpoint-url http://localhost:4566 sqs create-queue --queue-name outboxqueue
```

Run the application with local profile:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

Create books (GraphQL mutation), then watch logs:

- listener 1 succeeds consistently
- listener 2 fails randomly, then succeeds on a later delivery attempt

This demonstrates the broker value clearly: retries improve consumer-side reliability.

## Why This Still Is Not Enough

Even with SQS in place, there is still a dangerous window:

1. database write succeeds
2. app tries to publish event to SQS
3. app crashes/network fails before SQS accepts the message

In that path, data is committed but no message is ever sent. The broker cannot retry a message it never received.

This is the classic dual-write problem:

- write to database (system of record)
- write to broker (integration signal)

Without an atomic boundary across both writes, consistency gaps remain possible.

## Key Takeaway

SQS solves an important part of reliability:

- downstream processing retries
- durability once message is accepted by broker

But it does not solve producer atomicity with database state.

That final gap is solved by transactional outbox:

- write business data and outbox row in one DB transaction
- asynchronously forward outbox rows to SQS
- retry forward attempts until broker accepts

## What Comes Next (Part 3)

In Part 3, I will implement the full transactional outbox flow in this same project:

1. persist event payload in an `outbox_events` table in the same transaction as book writes
2. run an outbox dispatcher that reads pending rows and publishes to SQS
3. mark rows as processed (or retry with backoff) after publish outcome

That closes the producer-side reliability gap while keeping the broker-side retry benefits.
