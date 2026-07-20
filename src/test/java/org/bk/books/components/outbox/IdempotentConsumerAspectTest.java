package org.bk.books.components.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.BookEvents.BookCreatedEvent;
import org.bk.books.domain.entity.book.BookId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IdempotentConsumerAspectTest {
    @Mock
    private ConsumerMessageLogStore consumerMessageLogStore;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @InjectMocks
    private IdempotentConsumerAspect aspect;

    @Test
    void test_aroundEventListener_proceedsWhenEventSeenFirstTime() throws Throwable {
        BookCreatedEvent event = event();
        ReliableEventListener eventListener = annotation("listenerOne");
        when(joinPoint.getArgs()).thenReturn(new Object[] {event});
        when(consumerMessageLogStore.markProcessed("listenerOne", event.eventId()))
                .thenReturn(true);
        when(joinPoint.proceed()).thenReturn("processed");

        Object result = aspect.aroundEventListener(joinPoint, eventListener);

        assertThat(result).isEqualTo("processed");
        verify(joinPoint).proceed();
    }

    @Test
    void test_aroundEventListener_skipsWhenEventAlreadyProcessed() throws Throwable {
        BookCreatedEvent event = event();
        ReliableEventListener eventListener = annotation("listenerTwo");
        when(joinPoint.getArgs()).thenReturn(new Object[] {event});
        when(consumerMessageLogStore.alreadyProcessed(event.eventId(), "listenerTwo"))
                .thenReturn(true);

        Object result = aspect.aroundEventListener(joinPoint, eventListener);

        assertThat(result).isNull();
        verify(joinPoint, never()).proceed();
    }

    private static BookCreatedEvent event() {
        return new BookCreatedEvent(
                UUID.fromString("20e80ee5-b57e-4dbd-ae73-9099261498a4"),
                BookId.parse("09ac7f86-20a0-43e8-bf67-8fd787f56d5f"),
                "The Left Hand of Darkness",
                286,
                List.of(AuthorId.parse("a6a7f0d3-87bf-4bcc-95ca-c3596dbe4d08")));
    }

    private static ReliableEventListener annotation(String methodName) {
        try {
            Method method = AnnotationHolder.class.getDeclaredMethod(methodName, Object.class);
            return method.getAnnotation(ReliableEventListener.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static class AnnotationHolder {
        @ReliableEventListener(id = "listenerOne")
        public void listenerOne(Object ignored) {}

        @ReliableEventListener(id = "listenerTwo")
        public void listenerTwo(Object ignored) {}

        @ReliableEventListener(id = "listenerNoEvent")
        public void listenerNoEvent(Object ignored) {}
    }
}
