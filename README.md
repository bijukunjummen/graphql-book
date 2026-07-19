# GraphQL Book: Transactional Outbox Walkthrough (Parts 1-3)

This repository demonstrates, step by step, why the transactional outbox pattern is needed and how to implement it with Spring Boot, PostgreSQL, and SQS (via local endpoint).

## Series Overview

### Part 1: In-process event delivery can lose messages

- `BookCreatedEvent` is emitted from the write path.
- Two listeners consume it:
  - one reliable
  - one flaky (fails intermittently)
- Result: flaky listener drops events with no durable retry path.

Takeaway: in-process event dispatch is not enough for at-least-once delivery guarantees.

### Part 2: Broker retries help, but dual-write gap remains

- Event publishing moved to SQS.
- Flaky consumers can now succeed after retries/redelivery.
- But producer still does two non-atomic actions:
  1. write business data to DB
  2. publish event to broker

If app crashes between those two steps, the broker never receives the message.

Takeaway: broker improves consumer reliability, but does not solve producer atomicity.

### Part 3: Transactional outbox closes the producer-side gap

- Business write and outbox insert happen in the same DB transaction.
- A background dispatcher reads pending outbox rows and publishes to SQS.
- Rows are marked published on success, and failures are tracked for retries.

Takeaway: durable handoff in DB + async dispatch gives robust at-least-once semantics.

## Implementation in This Repository

### Event model and publish abstraction

- `src/main/java/org/bk/books/domain/entity/common/BaseEvent.java`
- `src/main/java/org/bk/books/components/outbox/OutboxMessagePublisher.java`

Services publish domain events through `OutboxMessagePublisher`.

### Outbox persistence

- Migration: `src/main/resources/db/migration/V0005__create_outbox_events.sql`
- Entity: `src/main/java/org/bk/books/entity/OutboxEventEntity.java`
- Store: `src/main/java/org/bk/books/components/outbox/OutboxEventStore.java`
- DB-backed publisher: `src/main/java/org/bk/books/components/outbox/DatabaseOutboxMessagePublisher.java`

The `outbox_events` table contains:

- `event_id`, `event_type`, `payload`
- `created_at`, `published_at`
- `attempts`, `last_error`

### Dispatch to broker

- Dispatcher: `src/main/java/org/bk/books/components/outbox/OutboxDispatcher.java`
- Broker abstraction: `src/main/java/org/bk/books/components/outbox/BrokerMessagePublisher.java`
- SQS publisher: `src/main/java/org/bk/books/components/outbox/SqsOutboxMessagePublisher.java`
- SQS listener + event dispatch: `src/main/java/org/bk/books/components/outbox/SqsOutboxMessageListener.java`
- Spring event dispatch adapter: `src/main/java/org/bk/books/components/outbox/SpringEventDispatcher.java`

### Wiring and scheduling

- Outbox config: `src/main/java/org/bk/books/config/OutboxConfig.java`
- Scheduling enabled: `src/main/java/org/bk/books/GraphqlBookApplication.java`
- Dispatcher poll interval: `src/main/resources/application.yml`
  - `books.outbox.dispatcher.fixed-delay-ms: 2000`

## End-to-End Flow

1. Client calls GraphQL mutation (`createBook`, `updateBookName`, etc).
2. Service writes business data to PostgreSQL.
3. Service writes event payload to `outbox_events` in the same transaction.
4. `OutboxDispatcher` polls pending outbox rows.
5. Dispatcher publishes envelope messages to SQS.
6. On success: row is marked published.
7. On failure: `attempts` and `last_error` are updated for retry.
8. SQS listener deserializes envelope and dispatches event to listeners.
9. Flaky listeners may fail transiently; SQS redelivery enables eventual success.

## Sequence Diagram

See:

- `docs/transactional-outbox-sequence-diagram.md`

## Local Run

### Prerequisites

- Java 25
- Docker
- AWS CLI (or equivalent) to create queue on local endpoint

### Start dependencies

```bash
docker compose up -d
```

This starts:

- PostgreSQL on `localhost:5432`
- Local AWS-compatible endpoint (floci) on `localhost:4566`

### Create SQS queue

```bash
aws --endpoint-url http://localhost:4566 sqs create-queue --queue-name outboxqueue
```

### Run the app

```bash
./gradlew bootRun
```

GraphQL endpoint:

- `http://localhost:8080/graphql`

GraphiQL:

- `http://localhost:8080/graphiql`

### Trigger and observe

Run a `createBook` mutation, then verify:

- a row appears in `outbox_events`
- dispatcher publishes it to SQS
- row transitions to published (`published_at` set)
- listeners process downstream event

## Related Posts

- Part 1: [Transactional Outbox Pattern — Part 1](https://bijukunjummen.medium.com/transactional-outbox-pattern-1-e5b6378ff6c9)
- Part 2: [Transactional Outbox Pattern — Part 2](https://bijukunjummen.medium.com/transactional-outbox-pattern-part-2-a0b94f76f252)
- Part 3: [Transactional Outbox Pattern - Part 3](https://medium.com/@bijukunjummen/transactional-outbox-pattern-part-3-7ea3e56bff6c)
