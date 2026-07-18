package org.bk.books.components.outbox;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import tools.jackson.databind.ObjectMapper;

public class SqsOutboxMessagePublisher implements OutboxMessagePublisher {
    private final SqsTemplate outboxSqsTemplate;
    private final ObjectMapper objectMapper;
    private final String sqsOutboxQueueName;

    public SqsOutboxMessagePublisher(
            SqsTemplate outboxSqsTemplate, ObjectMapper objectMapper, String sqsOutboxQueueName) {
        this.outboxSqsTemplate = outboxSqsTemplate;
        this.objectMapper = objectMapper;
        this.sqsOutboxQueueName = sqsOutboxQueueName;
    }

    public <T> void publish(T message) {
        outboxSqsTemplate.send(
                sqsOutboxQueueName,
                new Envelope(
                        objectMapper.writeValueAsString(message),
                        message.getClass().getName()));
    }
}
