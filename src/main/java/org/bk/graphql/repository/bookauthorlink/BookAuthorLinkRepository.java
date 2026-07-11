package org.bk.graphql.repository.bookauthorlink;

import org.bk.graphql.entity.BookAuthorLinkEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface BookAuthorLinkRepository extends BookAuthorLinkCustomRepository, CrudRepository<BookAuthorLinkEntity, UUID> {
    List<BookAuthorLinkEntity> findAllByBookIdIn(Set<UUID> bookIds);
    void deleteByBookId(UUID bookId);
    void deleteByBookIdAndAuthorIdNotIn(UUID bookId, Set<UUID> authorIds);
}
