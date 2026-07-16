package org.bk.books.application.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.BookId;

public interface BookAuthorLinkStore {
    void replaceAuthorsForBook(BookId bookId, List<AuthorId> authorIds, Instant now);

    Map<BookId, List<AuthorId>> findAuthorIdsByBookIds(List<BookId> bookIds);
}
