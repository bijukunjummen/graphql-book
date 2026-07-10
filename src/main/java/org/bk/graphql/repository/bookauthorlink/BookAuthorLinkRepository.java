package org.bk.graphql.repository.bookauthorlink;

import org.bk.graphql.entity.BookAuthorLinkEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface BookAuthorLinkRepository extends BookAuthorLinkCustomRepository, CrudRepository<BookAuthorLinkEntity, String> {
    List<BookAuthorLinkEntity> findAllByBookIdIn(Set<String> bookIds);
    void deleteByBookId(String bookId);
    void deleteByBookIdAndAuthorIdNotIn(String bookId, Set<String> authorIds);
}
