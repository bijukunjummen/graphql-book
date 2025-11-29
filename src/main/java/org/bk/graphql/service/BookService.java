package org.bk.graphql.service;

import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.Book;
import org.bk.graphql.domain.BookId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookService {
    Book createBook(CreateBookCommand command);
    Book createOrUpdateBook(CreateOrUpdateBookCommand command);
    Book updateBook(UpdateBookCommand command);
    Page<Book> getBooks(GetBooksQuery query);
    Page<Book> getBooks(Pageable pageable);
    Optional<Book> getBook(ById<BookId> query);
    List<Book> getBooks(ByIds<BookId> query);
    Map<BookId, List<Author>> getAuthorsForBooks(ByIds<BookId> ids);
}

