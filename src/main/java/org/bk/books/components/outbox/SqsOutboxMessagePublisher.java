package org.bk.books.components.outbox;

import io.awspring.cloud.sqs.operations.SqsTemplate;

public class SqsOutboxMessagePublisher implements BrokerMessagePublisher {
    private final SqsTemplate outboxSqsTemplate;
    private final String sqsOutboxQueueName;

    public SqsOutboxMessagePublisher(SqsTemplate outboxSqsTemplate, String sqsOutboxQueueName) {
        this.outboxSqsTemplate = outboxSqsTemplate;
        this.sqsOutboxQueueName = sqsOutboxQueueName;
    }

    @Override
    public void publish(Envelope message) {
        outboxSqsTemplate.send(sqsOutboxQueueName, message);
    }
}
