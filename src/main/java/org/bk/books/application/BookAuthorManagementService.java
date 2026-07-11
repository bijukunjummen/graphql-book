package org.bk.books.application;

import org.bk.books.common.query.ByIds;
import org.bk.books.domain.Author;
import org.bk.books.domain.BookId;

import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface BookAuthorManagementService {
    Map<BookId, List<Author>> getAuthorsForBooks(ByIds<BookId> ids) ;
}
