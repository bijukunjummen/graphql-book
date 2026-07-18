package org.bk.books.service.author;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.bk.books.common.query.ById;
import org.bk.books.common.query.ByIds;
import org.bk.books.components.outbox.OutboxMessagePublisher;
import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.author.AuthorEvents;
import org.bk.books.domain.entity.author.AuthorEvents.AuthorRenamedEvent;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.validation.AuthorName;
import org.bk.books.port.AuthorStore;
import org.bk.books.service.author.AuthorServiceCommands.CreateAuthorCommand;
import org.bk.books.service.author.AuthorServiceCommands.CreateOrUpdateAuthorCommand;
import org.bk.books.service.author.AuthorServiceCommands.UpdateAuthorNameCommand;
import org.bk.books.service.exception.DomainException;
import org.bk.books.util.Uuids;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorStore authorStore;
    private final OutboxMessagePublisher outboxMessagePublisher;
    private final Clock clock;
    private final Uuids uuids;

    public AuthorServiceImpl(
            AuthorStore authorStore, OutboxMessagePublisher outboxMessagePublisher, Clock clock, Uuids uuids) {
        this.authorStore = authorStore;
        this.outboxMessagePublisher = outboxMessagePublisher;
        this.clock = clock;
        this.uuids = uuids;
    }

    @Override
    @Transactional
    public Author createAuthor(CreateAuthorCommand command) {
        Instant now = clock.instant();
        Author author = Author.create(
                AuthorId.of(uuids.generateUuid()), AuthorName.of(command.name()).value(), now, now, 0);

        Author saved = authorStore.save(author);
        outboxMessagePublisher.publish(
                new AuthorEvents.AuthorCreatedEvent(uuids.generateUuid(), saved.id(), saved.name()));
        return saved;
    }

    @Override
    @Transactional
    public Author createOrUpdateAuthor(CreateOrUpdateAuthorCommand command) {
        Optional<Author> author = authorStore.findById(command.id());
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
                            command.version());
                    return authorStore.save(updatedAuthor);
                })
                .orElseGet(() -> createAuthor(new CreateAuthorCommand(command.name())));
    }

    @Transactional
    @Override
    public Author updateAuthorName(UpdateAuthorNameCommand command) {
        Author author = authorStore.findById(command.id()).orElseThrow(() -> new DomainException("Author not found"));
        Instant now = clock.instant();
        Author updatedAuthor = Author.create(
                author.id(), AuthorName.of(command.name()).value(), author.createdAt(), now, command.version());
        Author saved = authorStore.save(updatedAuthor);
        outboxMessagePublisher.publish(new AuthorRenamedEvent(uuids.generateUuid(), saved.id(), saved.name()));
        return saved;
    }

    @Override
    public Author getAuthor(ById<AuthorId> query) {
        return authorStore.findById(query.id()).orElseThrow(() -> new DomainException("Author not found"));
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
