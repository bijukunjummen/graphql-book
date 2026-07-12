package org.bk.books.repository.book;

import org.bk.books.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface BookCustomRepository {
  Page<BookEntity> getRankedBooks(Pageable pageable);
}
