package org.bk.books.application.port.out;

import java.util.List;
import java.util.Optional;
import org.bk.books.domain.entity.book.Book;
import org.bk.books.domain.entity.book.BookId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookStore {
  Book save(Book book);

  Optional<Book> findById(BookId id);

  List<Book> findAllByIds(List<BookId> ids);

  Page<Book> findAll(Pageable pageable);
}
