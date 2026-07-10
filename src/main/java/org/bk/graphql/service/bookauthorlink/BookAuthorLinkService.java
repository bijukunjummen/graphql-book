package org.bk.graphql.service.bookauthorlink;

import org.bk.graphql.common.query.ByIds;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.BookId;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BookAuthorLinkService {
    void replaceAuthorsForBook(BookId bookId, Set<AuthorId> authorIds);
    Map<BookId, List<AuthorId>> getAuthorIdsForBooks(ByIds<BookId> query);
}
