package org.bk.books.repository.author;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bk.books.database.repository.entity.AuthorEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorEntityRepository
        extends CrudRepository<AuthorEntity, UUID>, PagingAndSortingRepository<AuthorEntity, UUID> {
    List<AuthorEntity> findAllByIdIn(Collection<UUID> authorIds);
}
