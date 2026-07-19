package org.bk.books.components.outbox;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.bk.books.entity.OutboxEventEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OutboxDispatcherTest {
    @Mock
    private OutboxEventStore outboxEventStore;

    @Mock
    private BrokerMessagePublisher brokerMessagePublisher;

    private OutboxDispatcher outboxDispatcher;

    @BeforeEach
    void setup() {
        outboxDispatcher = new OutboxDispatcher(outboxEventStore, brokerMessagePublisher, 25);
    }

    @Test
    void test_dispatchPending_marksEventPublishedWhenBrokerSendSucceeds() {
        OutboxEventEntity event = new OutboxEventEntity(
                UUID.fromString("53fb6b66-0f91-4d91-96c8-1f44f4032bd2"),
                UUID.fromString("ec80d619-f349-4597-9854-326b6fbb6a14"),
                "org.bk.books.domain.entity.book.BookEvents$BookCreatedEvent",
                "{\"name\":\"Dune\"}",
                Instant.parse("2026-07-18T08:00:00Z"),
                null,
                0,
                null);
        when(outboxEventStore.fetchPending(25)).thenReturn(List.of(event));

        outboxDispatcher.dispatchPending();

        verify(brokerMessagePublisher).publish(new Envelope(event.payload(), event.eventType()));
        verify(outboxEventStore).markPublished(event.id());
    }

    @Test
    void test_dispatchPending_recordsFailureWhenBrokerSendFails() {
        OutboxEventEntity event = new OutboxEventEntity(
                UUID.fromString("58f346af-8d45-40b4-ad48-3ec7f9f8f1dd"),
                UUID.fromString("2df5dfd6-c7c6-4cbf-90f5-981d1a5c6f95"),
                "org.bk.books.domain.entity.book.BookEvents$BookCreatedEvent",
                "{\"name\":\"Hyperion\"}",
                Instant.parse("2026-07-18T09:00:00Z"),
                null,
                0,
                null);
        when(outboxEventStore.fetchPending(25)).thenReturn(List.of(event));
        doThrow(new RuntimeException("SQS unavailable"))
                .when(brokerMessagePublisher)
                .publish(new Envelope(event.payload(), event.eventType()));

        outboxDispatcher.dispatchPending();

        verify(outboxEventStore).recordFailure(event.id(), "SQS unavailable");
    }
}
