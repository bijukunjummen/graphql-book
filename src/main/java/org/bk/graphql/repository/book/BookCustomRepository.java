package org.bk.graphql.repository.book;

import org.bk.graphql.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookCustomRepository {
    Page<BookEntity> getRankedBooks(Pageable pageable);
}
