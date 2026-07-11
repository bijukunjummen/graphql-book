package org.bk.graphql.service.author;

import org.bk.graphql.application.port.out.AuthorStore;
import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.event.AuthorRenamedEvent;
import org.bk.graphql.domain.validation.AuthorName;
import org.bk.graphql.common.query.ById;
import org.bk.graphql.common.query.ByIds;
import org.bk.graphql.service.exception.DomainException;
import org.bk.graphql.util.Uuids;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorStore authorStore;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;
    private final Uuids uuids;

    public AuthorServiceImpl(AuthorStore authorStore, ApplicationEventPublisher eventPublisher, Clock clock, Uuids uuids) {
        this.authorStore = authorStore;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
        this.uuids = uuids;
    }

    @Override
    @Transactional
    public Author createAuthor(CreateAuthorCommand command) {
        Instant now = clock.instant();
        Author author = Author.create(
                AuthorId.of(uuids.generateUuid()),
                AuthorName.of(command.name()).value(),
                now,
                now,
                0
        );
        return authorStore.save(author);
    }

    @Override
    @Transactional
    public Author createOrUpdateAuthor(CreateOrUpdateAuthorCommand command) {
        Optional<Author> author = authorStore.findById(AuthorId.of(command.id()));
        Instant now = clock.instant();

        return author.map(existingAuthor -> {
            if (command.version() == 0) {
                return existingAuthor;
            }
            Author updatedAuthor = Author.create(
                    existingAuthor.id(),
                    AuthorName.of(command.name()).value(),
                    existingAuthor.createdAt(),
                    now,
                    command.version()
            );
            return authorStore.save(updatedAuthor);
        }).orElseGet(() -> {
            Author newAuthor = Author.create(
                    AuthorId.of(command.id()),
                    AuthorName.of(command.name()).value(),
                    now,
                    now,
                    0
            );
            return authorStore.save(newAuthor);
        });
    }

    @Transactional
    @Override
    public Author updateAuthorName(UpdateAuthorNameCommand command) {
        Author author = authorStore.findById(AuthorId.of(command.id()))
                .orElseThrow(() -> new DomainException("Author not found"));
        Instant now = clock.instant();
        Author updatedAuthor = Author.create(
                author.id(),
                AuthorName.of(command.name()).value(),
                author.createdAt(),
                now,
                command.version()
        );
        Author saved = authorStore.save(updatedAuthor);
        eventPublisher.publishEvent(new AuthorRenamedEvent(saved.id(), saved.name()));
        return saved;
    }

    @Override
    public Author getAuthor(ById<AuthorId> query) {
        return authorStore
                .findById(query.id())
                .orElseThrow(() -> new DomainException("Author not found"))
                ;
    }

    @Override
    public List<Author> getAuthors(ByIds<AuthorId> query) {
        return authorStore.findAllByIds(query.ids());
    }

    @Override
    public Page<Author> getAuthors(Pageable pageable) {
        return authorStore.findAll(pageable);
    }
}

