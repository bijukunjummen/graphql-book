package org.bk.books.components.outbox;

import org.bk.books.entity.OutboxEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

public class OutboxDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxDispatcher.class);
    private final OutboxEventStore outboxEventStore;
    private final BrokerMessagePublisher brokerMessagePublisher;
    private final int batchSize;

    public OutboxDispatcher(
            OutboxEventStore outboxEventStore, BrokerMessagePublisher brokerMessagePublisher, int batchSize) {
        this.outboxEventStore = outboxEventStore;
        this.brokerMessagePublisher = brokerMessagePublisher;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${books.outbox.dispatcher.fixed-delay-ms:2000}")
    @Transactional
    public void dispatchPending() {
        Flux.fromIterable(outboxEventStore.fetchPending(batchSize))
                .subscribe(event -> {
                    try {
                        LOGGER.info("Received event {}", event);
                        brokerMessagePublisher.publish(new Envelope(event.payload(), event.eventType()));
                        outboxEventStore.markPublished(event.eventId());
                    } catch (Exception ex) {
                        LOGGER.warn("Outbox publish failed for eventId={}", event.eventId(), ex);
                        outboxEventStore.recordFailure(event.eventId(), ex.getMessage());
                    }
                });
    }
}
