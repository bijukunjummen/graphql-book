package org.bk.books.listeners;

import java.util.concurrent.ThreadLocalRandom;
import org.bk.books.components.outbox.ReliableEventListener;
import org.bk.books.domain.entity.book.BookEvents.BookCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BookEventListeners {
    private static final Logger LOG = LoggerFactory.getLogger(BookEventListeners.class);

    @ReliableEventListener(id = "bookCreatedListener1")
    public void bookCreatedListener1(BookCreatedEvent bookCreatedEvent) {
        LOG.atInfo()
                .setMessage("Book created event from Listener 1: {}")
                .addArgument(bookCreatedEvent)
                .log();
    }

    @ReliableEventListener(id = "bookCreatedListener2")
    public void bookCreatedListener2(BookCreatedEvent bookCreatedEvent) {
        int value = ThreadLocalRandom.current().nextInt(100);
        if (value < 50) {
            throw new RuntimeException("Could not process bookCreatedEvent");
        }
        LOG.atInfo()
                .setMessage("Successfully processed book created event from Listener 2: {}")
                .addArgument(bookCreatedEvent)
                .log();
    }
}
