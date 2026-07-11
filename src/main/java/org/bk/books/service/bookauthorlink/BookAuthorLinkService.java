package org.bk.books.service.bookauthorlink;

import org.bk.books.common.query.ByIds;
import org.bk.books.domain.AuthorId;
import org.bk.books.domain.BookId;

import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface BookAuthorLinkService {
    Map<BookId, List<AuthorId>> getAuthorIdsForBooks(ByIds<BookId> query);
}
