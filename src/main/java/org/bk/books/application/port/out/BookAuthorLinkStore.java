package org.bk.books.application.port.out;

import org.bk.books.domain.AuthorId;
import org.bk.books.domain.BookId;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BookAuthorLinkStore {
    void replaceAuthorsForBook(BookId bookId, Set<AuthorId> authorIds, Instant now);

    Map<BookId, List<AuthorId>> findAuthorIdsByBookIds(List<BookId> bookIds);
}
