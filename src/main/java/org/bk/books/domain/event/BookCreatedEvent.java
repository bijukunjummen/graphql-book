package org.bk.books.domain.event;

import java.util.List;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.BookId;

public record BookCreatedEvent(BookId bookId, String name, int pageCount, List<AuthorId> authorIds) {}
