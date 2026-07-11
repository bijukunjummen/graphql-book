package org.bk.books.repository.author;

import org.bk.books.entity.AuthorEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuthorRepository extends CrudRepository<AuthorEntity, UUID>, PagingAndSortingRepository<AuthorEntity, UUID> {
    List<AuthorEntity> findAllByIdIn(Collection<UUID> authorIds);
}

