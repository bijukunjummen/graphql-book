package org.bk.books.service.book;

import java.util.List;
import java.util.Optional;
import org.bk.books.common.query.ById;
import org.bk.books.common.query.ByIds;
import org.bk.books.domain.entity.book.Book;
import org.bk.books.domain.entity.book.BookId;
import org.bk.books.service.book.BookCommands.CreateBookCommand;
import org.bk.books.service.book.BookCommands.CreateOrUpdateBookCommand;
import org.bk.books.service.book.BookCommands.UpdateBookCommand;
import org.bk.books.service.book.BookCommands.UpdateBookNameCommand;
import org.bk.books.service.book.BookQueries.GetBooksQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Book createBook(CreateBookCommand command);

    Book createOrUpdateBook(CreateOrUpdateBookCommand command);

    Book updateBook(UpdateBookCommand command);

    Book updateBookName(UpdateBookNameCommand command);

    Page<Book> getBooks(GetBooksQuery query);

    Page<Book> getBooks(Pageable pageable);

    Optional<Book> getBook(ById<BookId> query);

    List<Book> getBooks(ByIds<BookId> query);
}
