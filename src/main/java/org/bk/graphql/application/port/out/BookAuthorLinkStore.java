package org.bk.graphql.application.port.out;

import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.BookId;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BookAuthorLinkStore {
    void replaceAuthorsForBook(BookId bookId, Set<AuthorId> authorIds, Instant now);

    Map<BookId, List<AuthorId>> findAuthorIdsByBookIds(List<BookId> bookIds);
}
