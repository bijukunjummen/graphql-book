package org.bk.graphql.service.bookauthorlink;

import org.bk.graphql.common.query.ByIds;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.BookId;

import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface BookAuthorLinkService {
    Map<BookId, List<AuthorId>> getAuthorIdsForBooks(ByIds<BookId> query);
}
