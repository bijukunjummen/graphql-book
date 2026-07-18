package org.bk.books.domain.entity.book;

import java.util.List;
import java.util.UUID;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.common.BaseEvent;

public interface BookEvents {
    record BookCreatedEvent(UUID eventId, BookId bookId, String name, int pageCount, List<AuthorId> authorIds)
            implements BaseEvent {}

    record BookAuthorsUpdatedEvent(
            UUID eventId, BookId bookId, List<AuthorId> previousAuthors, List<AuthorId> newAuthors)
            implements BaseEvent {}

    record BookNameUpdatedEvent(UUID eventId, BookId bookId, String oldName, String newName) implements BaseEvent {}

    record BookUpdatedEvent(UUID eventId, BookId bookId, String name, int pageCount, List<AuthorId> authorIds)
            implements BaseEvent {}
}
