package org.bk.books.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.floci.testcontainers.FlociContainer;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.bk.books.application.BookAuthorManagementService;
import org.bk.books.domain.entity.book.BookId;
import org.bk.books.service.book.BookCommands.CreateBookCommand;
import org.bk.books.service.event.BookCreatedEventProcessingTracker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@SpringBootTest(
        properties = {"books.demo.flaky-listener.enabled=true", "spring.main.allow-bean-definition-overriding=true"})
@Testcontainers
class BookCreatedEventLossDemoITest {
    @Autowired
    private BookAuthorManagementService bookAuthorManagementService;

    @Autowired
    private BookCreatedEventProcessingTracker processingTracker;

    @Test
    void test_bookCreatedEventGetsLostForFlakyListener() {
        processingTracker.clear();

        Set<BookId> createdBookIds = Set.of(
                createBook("event-demo-1"),
                createBook("event-demo-2"),
                createBook("event-demo-3"),
                createBook("event-demo-4"));

        Set<BookId> reliableProcessedBookIds = processingTracker.reliableProcessedBookIds();
        Set<BookId> flakyProcessedBookIds = processingTracker.flakyProcessedBookIds();

        assertThat(reliableProcessedBookIds).containsExactlyElementsOf(createdBookIds);
        assertThat(flakyProcessedBookIds).hasSizeLessThan(reliableProcessedBookIds.size());
    }

    private BookId createBook(String bookName) {
        return bookAuthorManagementService
                .createBook(new CreateBookCommand(bookName, 120, List.of()))
                .id();
    }

    @TestConfiguration
    static class SqsTestConfiguration {
        @Bean
        public SqsAsyncClient sqsAsyncClient() {
            return SqsAsyncClient.builder()
                    .endpointOverride(URI.create(flociContainer.getEndpoint()))
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
                    .region(Region.US_EAST_1)
                    .build();
        }
    }

    @ServiceConnection
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.5-bullseye");

    @Container
    private static final FlociContainer flociContainer = new FlociContainer();
}
