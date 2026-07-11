package org.bk.books.domain.event;

import org.bk.books.domain.AuthorId;
import org.bk.books.domain.BookId;

import java.util.List;

public record BookCreatedEvent(BookId bookId, List<AuthorId> authorIds) {
}
