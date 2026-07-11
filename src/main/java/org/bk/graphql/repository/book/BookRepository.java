package org.bk.graphql.repository.book;

import org.bk.graphql.entity.BookEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookRepository extends CrudRepository<BookEntity, UUID>, PagingAndSortingRepository<BookEntity, UUID>, BookCustomRepository {
}

