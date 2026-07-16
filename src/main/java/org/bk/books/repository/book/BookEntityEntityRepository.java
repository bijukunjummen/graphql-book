package org.bk.books.repository.book;

import java.util.UUID;
import org.bk.books.entity.BookEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookEntityRepository
        extends CrudRepository<BookEntity, UUID>, PagingAndSortingRepository<BookEntity, UUID>, BookCustomRepository {}
