package org.bk.graphql.application.port.out;

import org.bk.graphql.domain.Book;
import org.bk.graphql.domain.BookId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookStore {
    Book save(Book book);

    Optional<Book> findById(BookId id);

    List<Book> findAllByIds(List<BookId> ids);

    Page<Book> findAll(Pageable pageable);
}
