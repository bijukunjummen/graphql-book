package org.bk.books.config;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.bk.books.components.outbox.EventDispatcher;
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
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder().build();
    }

    @Bean
    public SqsTemplate outboxSqsTemplate(SqsAsyncClient sqsAsyncClient) {
        return SqsTemplate.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(t -> t.defaultQueue(OUTBOX_QUEUE_NAME))
                .build();
    }

    @Bean
    public OutboxMessagePublisher outboxMessagePublisher(SqsTemplate outboxSqsTemplate, ObjectMapper objectMapper) {
        return new SqsOutboxMessagePublisher(outboxSqsTemplate, objectMapper, OUTBOX_QUEUE_NAME);
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
