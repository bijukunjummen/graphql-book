package org.bk.graphql.service.book;

import org.bk.graphql.common.query.ById;
import org.bk.graphql.common.query.ByIds;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.Book;
import org.bk.graphql.domain.BookId;
import org.bk.graphql.domain.ImmutableBook;
import org.bk.graphql.entity.BookAuthorLinkEntity;
import org.bk.graphql.entity.BookEntity;
import org.bk.graphql.repository.book.BookRepository;
import org.bk.graphql.repository.bookauthorlink.BookAuthorLinkRepository;
import org.bk.graphql.service.book.BookCommands.CreateBookCommand;
import org.bk.graphql.service.book.BookCommands.CreateOrUpdateBookCommand;
import org.bk.graphql.service.book.BookCommands.UpdateBookCommand;
import org.bk.graphql.service.book.BookCommands.UpdateBookNameCommand;
import org.bk.graphql.service.book.BookQueries.GetBooksQuery;
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
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookAuthorLinkRepository bookAuthorLinkRepository;
    private final Clock clock;
    private final Uuids uuids;

    public BookServiceImpl(
            BookRepository bookRepository,
            BookAuthorLinkRepository bookAuthorLinkRepository,
            Clock clock,
            Uuids uuids) {
        this.bookRepository = bookRepository;
        this.bookAuthorLinkRepository = bookAuthorLinkRepository;
        this.clock = clock;
        this.uuids = uuids;
    }

    @Override
    @Transactional
    public Book createBook(CreateBookCommand command) {
        UUID bookId = uuids.generateUuid();

        Instant now = clock.instant();
        BookEntity book = new BookEntity(
                bookId,
                command.name(),
                command.pageCount(),
                now,
                now,
                0
        );
        BookEntity savedBook = bookRepository.save(book);
        final List<BookAuthorLinkEntity> bookAuthorLinks = command.authors().stream().map(authorId -> new BookAuthorLinkEntity(uuids.generateUuid(), savedBook.id(), authorId.id(), now, now))
                .distinct()
                .toList();
        bookAuthorLinkRepository.upsertAll(bookAuthorLinks);
        Book withoutAuthors = savedBook.toModel();
        return ImmutableBook.builder().from(withoutAuthors)
                .authors(bookAuthorLinks.stream().map(link -> AuthorId.of(link.authorId())).toList())
                .build();
    }

    @Override
    @Transactional
    public Book createOrUpdateBook(CreateOrUpdateBookCommand command) {
        Optional<BookEntity> book = bookRepository.findById(command.id());

        Instant now = clock.instant();
        return book.map(existingBook -> {
            if (command.version() == 0) {
                return existingBook.toModel();
            }
            BookEntity updatedBook = new BookEntity(
                    book.get().id(),
                    command.name(),
                    command.pageCount(),
                    existingBook.createdAt(),
                    now,
                    command.version()
            );
            BookEntity savedBook = bookRepository.save(updatedBook);
            List<BookAuthorLinkEntity> bookAuthorLinks = command.authors().stream().map(authorId ->
                    new BookAuthorLinkEntity(uuids.generateUuid(), updatedBook.id(), authorId.id(), now, now)).toList();
            bookAuthorLinkRepository.upsertAll(bookAuthorLinks);
            Book withoutAuthors = savedBook.toModel();
            return ImmutableBook.builder().from(withoutAuthors)
                    .authors(bookAuthorLinks.stream().map(link -> AuthorId.of(link.authorId())).toList())
                    .build();
        }).orElseGet(() -> {
            BookEntity newBook = new BookEntity(
                    command.id(),
                    command.name(),
                    command.pageCount(),
                    now,
                    now,
                    0
            );
            BookEntity savedBook = bookRepository.save(newBook);
            final List<BookAuthorLinkEntity> bookAuthorLinks = command.authors().stream().map(authorId -> new BookAuthorLinkEntity(uuids.generateUuid(), savedBook.id(), authorId.id(), now, now))
                    .distinct()
                    .toList();
            bookAuthorLinkRepository.upsertAll(bookAuthorLinks);
            Book withoutAuthors = savedBook.toModel();
            return ImmutableBook.builder().from(withoutAuthors)
                    .authors(bookAuthorLinks.stream().map(link -> AuthorId.of(link.authorId())).toList())
                    .build();
        });
    }

    @Override
    public Book updateBook(UpdateBookCommand command) {
        BookEntity book = bookRepository.findById(command.id())
                .orElseThrow(() -> new DomainException("Book not found"));
        Instant now = clock.instant();
        BookEntity updatedBook = new BookEntity(
                book.id(),
                command.name(),
                command.pageCount(),
                book.createdAt(),
                now,
                command.version()
        );
        BookEntity savedBook = bookRepository.save(updatedBook);
        List<BookAuthorLinkEntity> bookAuthorLinks = command.authors().stream().map(authorId ->
                new BookAuthorLinkEntity(uuids.generateUuid(), updatedBook.id(), authorId.id(), now, now)).toList();
        bookAuthorLinkRepository.upsertAll(bookAuthorLinks);
        Book withoutAuthors = savedBook.toModel();
        return ImmutableBook.builder().from(withoutAuthors)
                .authors(bookAuthorLinks.stream().map(link -> AuthorId.of(link.authorId())).toList())
                .build();
    }

    @Override
    public Book updateBookName(UpdateBookNameCommand command) {
        BookEntity book = bookRepository.findById(command.id())
                .orElseThrow(() -> new DomainException("Book not found"));
        BookEntity updatedBook = new BookEntity(
                book.id(),
                command.name(),
                book.pageCount(),
                book.createdAt(),
                clock.instant(),
                command.version()
        );
        BookEntity savedBook = bookRepository.save(updatedBook);
        return savedBook.toModel();
    }

    @Override
    public Page<Book> getBooks(GetBooksQuery query) {
        return bookRepository
                .findAll(Pageable.ofSize(query.size()).withPage(query.page()))
                .map(BookEntity::toModel);
    }

    @Override
    public Page<Book> getBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(BookEntity::toModel);
    }

    @Override
    public Optional<Book> getBook(ById<BookId> query) {
        BookId bookId = query.id();
        return bookRepository.findById(bookId.id()).map(BookEntity::toModel);
    }

    @Override
    public List<Book> getBooks(ByIds<BookId> query) {
        return StreamSupport.stream(
                bookRepository.findAllById(
                        query.ids().stream().map(BookId::id).toList()
                ).spliterator(), false
        ).map(BookEntity::toModel).collect(java.util.stream.Collectors.toList());
    }
}
