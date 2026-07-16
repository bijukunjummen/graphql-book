package org.bk.books.port;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.author.AuthorId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorStore {
    Author save(Author author);

    Optional<Author> findById(AuthorId id);

    List<Author> findAllByIds(Collection<AuthorId> ids);

    Page<Author> findAll(Pageable pageable);
}
