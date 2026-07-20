package org.bk.books.config;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.bk.books.components.outbox.BrokerMessagePublisher;
import org.bk.books.components.outbox.DatabaseOutboxMessagePublisher;
import org.bk.books.components.outbox.EventDispatcher;
import org.bk.books.components.outbox.OutboxDispatcher;
import org.bk.books.components.outbox.OutboxEventStore;
import org.bk.books.components.outbox.OutboxMessagePublisher;
import org.bk.books.components.outbox.SpringEventDispatcher;
import org.bk.books.components.outbox.SqsOutboxMessageListener;
import org.bk.books.components.outbox.SqsOutboxMessagePublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class OutboxConfig {

    private static final String OUTBOX_QUEUE_NAME = "outboxqueue";

    @Bean
    public SqsTemplate outboxSqsTemplate(SqsAsyncClient sqsAsyncClient) {
        return SqsTemplate.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(t -> t.defaultQueue(OUTBOX_QUEUE_NAME))
                .build();
    }

    @Bean
    public OutboxMessagePublisher outboxMessagePublisher(OutboxEventStore outboxEventStore, ObjectMapper objectMapper) {
        return new DatabaseOutboxMessagePublisher(outboxEventStore, objectMapper);
    }

    @Bean
    public BrokerMessagePublisher brokerMessagePublisher(SqsTemplate outboxSqsTemplate) {
        return new SqsOutboxMessagePublisher(outboxSqsTemplate, OUTBOX_QUEUE_NAME);
    }

    @Bean
    public OutboxDispatcher outboxDispatcher(
            OutboxEventStore outboxEventStore, BrokerMessagePublisher brokerMessagePublisher) {
        return new OutboxDispatcher(outboxEventStore, brokerMessagePublisher, 25);
    }

    @Bean
    public EventDispatcher eventDispatcher(ApplicationEventPublisher applicationEventPublisher) {
        return new SpringEventDispatcher(applicationEventPublisher);
    }

    @Bean
    public SqsOutboxMessageListener outboxMessageListener(ObjectMapper objectMapper, EventDispatcher eventDispatcher) {
        return new SqsOutboxMessageListener(objectMapper, eventDispatcher);
    }
}
