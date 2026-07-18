package org.bk.books.domain.entity.author;

import java.util.UUID;
import org.bk.books.domain.entity.common.BaseEvent;

public interface AuthorEvents {
    record AuthorRenamedEvent(UUID eventId, AuthorId authorId, String name) implements BaseEvent {}

    record AuthorCreatedEvent(UUID eventId, AuthorId authorId, String name) implements BaseEvent {}
}
