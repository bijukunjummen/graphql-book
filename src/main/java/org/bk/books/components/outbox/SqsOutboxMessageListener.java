package org.bk.books.components.outbox;

import io.awspring.cloud.sqs.annotation.SqsListener;
import tools.jackson.databind.ObjectMapper;

public class SqsOutboxMessageListener {
    private final ObjectMapper objectMapper;
    private final EventDispatcher eventDispatcher;

    public SqsOutboxMessageListener(ObjectMapper objectMapper, EventDispatcher eventDispatcher) {
        this.objectMapper = objectMapper;
        this.eventDispatcher = eventDispatcher;
    }

    @SqsListener("outboxqueue")
    public void listen(Envelope message) {
        try {
            Object event = objectMapper.readValue(message.event(), Class.forName(message.eventType()));
            eventDispatcher.dispatch(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
