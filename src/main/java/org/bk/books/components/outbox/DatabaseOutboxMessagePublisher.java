package org.bk.books.components.outbox;

import org.bk.books.domain.entity.common.BaseEvent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

public class DatabaseOutboxMessagePublisher implements OutboxMessagePublisher {
    private final OutboxEventStore outboxEventStore;
    private final ObjectMapper objectMapper;

    public DatabaseOutboxMessagePublisher(OutboxEventStore outboxEventStore, ObjectMapper objectMapper) {
        this.outboxEventStore = outboxEventStore;
        this.objectMapper = objectMapper;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void publish(BaseEvent message) {
        outboxEventStore.append(
                message.eventId(), message.getClass().getName(), objectMapper.writeValueAsString(message));
    }
}
