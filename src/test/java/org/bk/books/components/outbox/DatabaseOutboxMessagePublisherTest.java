package org.bk.books.components.outbox;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.BookEvents.BookCreatedEvent;
import org.bk.books.domain.entity.book.BookId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DatabaseOutboxMessagePublisherTest {
    @Mock
    private OutboxEventStore outboxEventStore;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DatabaseOutboxMessagePublisher publisher;

    @Test
    void test_publish_persistsSerializedEventToOutboxStore() throws Exception {
        BookCreatedEvent event = new BookCreatedEvent(
                UUID.fromString("f18ffeb8-8ab5-4f62-91e2-95a30c736f3f"),
                BookId.parse("f4341c4f-1c35-4df6-a5f4-3160f3455a4e"),
                "The Dispossessed",
                341,
                List.of(AuthorId.parse("5988efba-938f-4d3c-9faa-4a3bb7d57252")));
        when(objectMapper.writeValueAsString(event)).thenReturn("{\"name\":\"The Dispossessed\"}");

        publisher.publish(event);

        verify(outboxEventStore).append(event.eventId(), event.getClass().getName(), "{\"name\":\"The Dispossessed\"}");
    }
}
