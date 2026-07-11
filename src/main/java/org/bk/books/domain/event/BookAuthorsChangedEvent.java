package org.bk.books.domain.event;

import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.BookId;

import java.util.List;

public record BookAuthorsChangedEvent(BookId bookId, List<AuthorId> authorIds) {
}
