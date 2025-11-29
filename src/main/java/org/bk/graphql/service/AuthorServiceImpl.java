package org.bk.graphql.service;

import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.entity.AuthorEntity;
import org.bk.graphql.repository.AuthorRepository;
import org.bk.graphql.service.exception.DomainException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Author createAuthor(CreateAuthorCommand command) {
        AuthorEntity author = new AuthorEntity(
            UUID.randomUUID().toString(),
            command.name(),
            0
        );
        AuthorEntity savedAuthor = authorRepository.save(author);
        return savedAuthor.toModel();
    }

    @Override
    public Author createOrUpdateAuthor(CreateOrUpdateAuthorCommand command) {
        var author = authorRepository.findById(command.id());

        if (author.isPresent()) {
            if (command.version() != 0) {
                AuthorEntity updatedAuthor = new AuthorEntity(
                    author.get().id(),
                    command.name(),
                    command.version()
                );
                authorRepository.save(updatedAuthor);
            }
        } else {
            AuthorEntity newAuthor = new AuthorEntity(
                command.id(),
                command.name(),
                0
            );
            authorRepository.save(newAuthor);
        }
        AuthorEntity authorEntity = authorRepository.findById(command.id())
            .orElseThrow();
        return authorEntity.toModel();
    }

    @Override
    public Author updateAuthor(UpdateAuthorCommand command) {
        AuthorEntity author = authorRepository.findById(command.id())
            .orElseThrow(() -> new DomainException("Author not found"));

        AuthorEntity updatedAuthor = new AuthorEntity(
            author.id(),
            command.name(),
            command.version()
        );
        AuthorEntity updatedAuthorEntity = authorRepository.save(updatedAuthor);
        return updatedAuthorEntity.toModel();
    }

    @Transactional
    @Override
    public Author updateAuthorName(UpdateAuthorNameCommand command) {
        AuthorEntity author = authorRepository.findById(command.id())
            .orElseThrow(() -> new DomainException("Author not found"));
        AuthorEntity updatedAuthor = new AuthorEntity(
            author.id(),
            command.name(),
            command.version()
        );
        AuthorEntity updatedEntity = authorRepository.save(updatedAuthor);
        return updatedEntity.toModel();
    }

    @Override
    public Page<Author> getAuthors(GetAuthorsQuery query) {
        return authorRepository
            .findAll(Pageable.ofSize(query.size()).withPage(query.page()))
            .map(AuthorEntity::toModel);
    }

    @Override
    public Author getAuthor(ById<AuthorId> query) {
        return authorRepository
            .findById(query.id().id())
            .orElseThrow(() -> new DomainException("Author not found"))
            .toModel();
    }

    @Override
    public List<Author> getAuthors(ByIds<AuthorId> query) {
        return StreamSupport.stream(
            authorRepository.findAllById(
                query.ids().stream().map(AuthorId::id).toList()
            ).spliterator(), false
        ).map(AuthorEntity::toModel).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Page<Author> getAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable)
            .map(AuthorEntity::toModel);
    }
}

