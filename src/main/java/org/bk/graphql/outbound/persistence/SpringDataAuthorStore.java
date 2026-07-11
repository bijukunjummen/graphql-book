package org.bk.graphql.outbound.persistence;

import org.bk.graphql.application.port.out.AuthorStore;
import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.entity.AuthorEntity;
import org.bk.graphql.repository.author.AuthorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SpringDataAuthorStore implements AuthorStore {
    private final AuthorRepository authorRepository;

    public SpringDataAuthorStore(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Author save(Author author) {
        return authorRepository.save(AuthorEntity.fromModel(author)).toModel();
    }

    @Override
    public Optional<Author> findById(AuthorId id) {
        return authorRepository.findById(id.id()).map(AuthorEntity::toModel);
    }

    @Override
    public List<Author> findAllByIds(Collection<AuthorId> ids) {
        Set<java.util.UUID> authorIds = ids.stream().map(AuthorId::id).collect(Collectors.toSet());
        return authorRepository.findAllByIdIn(authorIds).stream().map(AuthorEntity::toModel).toList();
    }

    @Override
    public Page<Author> findAll(Pageable pageable) {
        return authorRepository.findAll(pageable).map(AuthorEntity::toModel);
    }
}
