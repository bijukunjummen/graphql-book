package org.bk.graphql.application.port.out;

import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AuthorStore {
    Author save(Author author);

    Optional<Author> findById(AuthorId id);

    List<Author> findAllByIds(Collection<AuthorId> ids);

    Page<Author> findAll(Pageable pageable);
}
