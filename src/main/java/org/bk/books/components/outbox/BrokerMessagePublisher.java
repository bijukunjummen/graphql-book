package org.bk.books.components.outbox;

public interface BrokerMessagePublisher {
    void publish(Envelope message);
}
