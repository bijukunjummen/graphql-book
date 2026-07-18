package org.bk.books.components.outbox;

public interface OutboxMessagePublisher {
    <T> void publish(T message);
}
