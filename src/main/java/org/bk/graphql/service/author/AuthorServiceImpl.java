package org.bk.graphql.service.author;

import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.entity.AuthorEntity;
import org.bk.graphql.repository.author.AuthorRepository;
import org.bk.graphql.common.query.ById;
import org.bk.graphql.common.query.ByIds;
import org.bk.graphql.service.exception.DomainException;
import org.bk.graphql.util.Uuids;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final Clock clock;
    private final Uuids uuids;

    public AuthorServiceImpl(AuthorRepository authorRepository, Clock clock, Uuids uuids) {
        this.authorRepository = authorRepository;
        this.clock = clock;
        this.uuids = uuids;
    }

    @Override
    public Author createAuthor(CreateAuthorCommand command) {
        Instant now = clock.instant();
        AuthorEntity author = new AuthorEntity(
                uuids.generateUuid().toString(),
                command.name(),
                now,
                now,
                0
        );
        AuthorEntity savedAuthor = authorRepository.save(author);
        return savedAuthor.toModel();
    }

    @Override
    public Author createOrUpdateAuthor(CreateOrUpdateAuthorCommand command) {
        Optional<AuthorEntity> author = authorRepository.findById(command.id());
        Instant now = clock.instant();

        return author.map(existingAuthor -> {
            if (command.version() == 0) {
                return existingAuthor.toModel();
            }
            AuthorEntity updatedAuthor = new AuthorEntity(
                    author.get().id(),
                    command.name(),
                    existingAuthor.createdAt(),
                    now,
                    command.version()
            );
            return authorRepository.save(updatedAuthor).toModel();
        }).orElseGet(() -> {
            AuthorEntity newAuthor = new AuthorEntity(
                    command.id(),
                    command.name(),
                    now,
                    now,
                    0
            );
            return authorRepository.save(newAuthor).toModel();
        });
    }

    @Transactional
    @Override
    public Author updateAuthorName(UpdateAuthorNameCommand command) {
        AuthorEntity author = authorRepository.findById(command.id())
                .orElseThrow(() -> new DomainException("Author not found"));
        Instant now = clock.instant();
        AuthorEntity updatedAuthor = new AuthorEntity(
                author.id(),
                command.name(),
                author.createdAt(),
                now,
                command.version()
        );
        AuthorEntity updatedEntity = authorRepository.save(updatedAuthor);
        return updatedEntity.toModel();
    }

    @Override
    public Author getAuthor(ById<AuthorId> query) {
        return authorRepository
                .findById(query.id().toString())
                .orElseThrow(() -> new DomainException("Author not found"))
                .toModel();
    }

    @Override
    public List<Author> getAuthors(ByIds<AuthorId> query) {
        return authorRepository
                .findAllByIdIn(query.ids().stream().map(id -> id.id().toString()).collect(Collectors.toSet()))
                .stream().map(AuthorEntity::toModel)
                .toList();
    }

    @Override
    public Page<Author> getAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable)
                .map(AuthorEntity::toModel);
    }
}

