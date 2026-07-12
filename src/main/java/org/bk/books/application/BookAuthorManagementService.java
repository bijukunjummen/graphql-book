package org.bk.books.application;

import java.util.List;
import java.util.Map;
import org.bk.books.common.query.ByIds;
import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.book.BookId;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface BookAuthorManagementService {
  Map<BookId, List<Author>> getAuthorsForBooks(ByIds<BookId> ids);
}
