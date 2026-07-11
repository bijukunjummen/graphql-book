package org.bk.books.application.port.out;

import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.BookId;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface BookAuthorLinkStore {
    void replaceAuthorsForBook(BookId bookId, List<AuthorId> authorIds, Instant now);

    Map<BookId, List<AuthorId>> findAuthorIdsByBookIds(List<BookId> bookIds);
}
