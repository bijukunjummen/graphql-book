package org.bk.books.application;

import java.util.List;
import java.util.Map;
import org.bk.books.common.query.ByIds;
import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.Book;
import org.bk.books.domain.entity.book.BookId;
import org.bk.books.service.book.BookCommands;
import org.bk.books.service.book.BookService;

public interface BookAuthorManagementService extends BookService {
    Map<BookId, List<Author>> getAuthorsForBooks(ByIds<BookId> ids);

    Book updateBookAuthors(BookCommands.UpdateBookAuthorsCommand command);

    Map<BookId, List<AuthorId>> getAuthorIdsForBooks(ByIds<BookId> query);
}
