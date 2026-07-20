package org.bk.books.outbound.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.database.repository.entity.AuthorEntity;
import org.bk.books.port.AuthorStore;
import org.bk.books.repository.author.AuthorEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class SpringDataAuthorStore implements AuthorStore {
    private final AuthorEntityRepository authorEntityRepository;

    public SpringDataAuthorStore(AuthorEntityRepository authorEntityRepository) {
        this.authorEntityRepository = authorEntityRepository;
    }

    @Override
    public Author save(Author author) {
        return authorEntityRepository.save(AuthorEntity.fromModel(author)).toModel();
    }

    @Override
    public Optional<Author> findById(AuthorId id) {
        return authorEntityRepository.findById(id.id()).map(AuthorEntity::toModel);
    }

    @Override
    public List<Author> findAllByIds(Collection<AuthorId> ids) {
        Set<java.util.UUID> authorIds = ids.stream().map(AuthorId::id).collect(Collectors.toSet());
        return authorEntityRepository.findAllByIdIn(authorIds).stream()
                .map(AuthorEntity::toModel)
                .toList();
    }

    @Override
    public Page<Author> findAll(Pageable pageable) {
        return authorEntityRepository.findAll(pageable).map(AuthorEntity::toModel);
    }
}
