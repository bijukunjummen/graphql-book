package org.bk.graphql.service.book;

import org.bk.graphql.application.port.out.BookAuthorLinkStore;
import org.bk.graphql.application.port.out.BookStore;
import org.bk.graphql.common.query.ById;
import org.bk.graphql.common.query.ByIds;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.Book;
import org.bk.graphql.domain.BookId;
import org.bk.graphql.domain.ImmutableBook;
import org.bk.graphql.domain.event.BookAuthorsChangedEvent;
import org.bk.graphql.domain.event.BookCreatedEvent;
import org.bk.graphql.domain.validation.BookName;
import org.bk.graphql.domain.validation.PageCount;
import org.bk.graphql.service.book.BookCommands.CreateBookCommand;
import org.bk.graphql.service.book.BookCommands.CreateOrUpdateBookCommand;
import org.bk.graphql.service.book.BookCommands.UpdateBookCommand;
import org.bk.graphql.service.book.BookCommands.UpdateBookNameCommand;
import org.bk.graphql.service.book.BookQueries.GetBooksQuery;
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
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    private final BookStore bookStore;
    private final BookAuthorLinkStore bookAuthorLinkStore;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;
    private final Uuids uuids;

    public BookServiceImpl(
            BookStore bookStore,
            BookAuthorLinkStore bookAuthorLinkStore,
            ApplicationEventPublisher eventPublisher,
            Clock clock,
            Uuids uuids) {
        this.bookStore = bookStore;
        this.bookAuthorLinkStore = bookAuthorLinkStore;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
        this.uuids = uuids;
    }

    @Override
    @Transactional
    public Book createBook(CreateBookCommand command) {
        Instant now = clock.instant();
        Book newBook = Book.create(
                BookId.of(uuids.generateUuid()),
                BookName.of(command.name()).value(),
                List.of(),
                PageCount.of(command.pageCount()).value(),
                now,
                now,
                0
        );
        Book saved = persistBookWithAuthors(
                newBook,
                command.authors().stream().distinct().toList(),
                now
        );
        eventPublisher.publishEvent(new BookCreatedEvent(saved.id(), saved.authors()));
        return saved;
    }

    @Override
    @Transactional
    public Book createOrUpdateBook(CreateOrUpdateBookCommand command) {
        Optional<Book> book = bookStore.findById(BookId.of(command.id()));

        Instant now = clock.instant();
        return book.map(existingBook -> {
            if (command.version() == 0) {
                return enrichWithAuthors(existingBook);
            }
            Book updatedBook = Book.create(
                    existingBook.id(),
                    BookName.of(command.name()).value(),
                    List.of(),
                    PageCount.of(command.pageCount()).value(),
                    existingBook.createdAt(),
                    now,
                    command.version()
            );
            Book saved = persistBookWithAuthors(
                    updatedBook,
                    command.authors().stream().distinct().toList(),
                    now
            );
            eventPublisher.publishEvent(new BookAuthorsChangedEvent(saved.id(), saved.authors()));
            return saved;
        }).orElseGet(() -> {
            Book newBook = Book.create(
                    BookId.of(command.id()),
                    BookName.of(command.name()).value(),
                    List.of(),
                    PageCount.of(command.pageCount()).value(),
                    now,
                    now,
                    0
            );
            Book saved = persistBookWithAuthors(
                    newBook,
                    command.authors().stream().distinct().toList(),
                    now
            );
            eventPublisher.publishEvent(new BookCreatedEvent(saved.id(), saved.authors()));
            return saved;
        });
    }

    @Override
    @Transactional
    public Book updateBook(UpdateBookCommand command) {
        Book book = bookStore.findById(BookId.of(command.id()))
                .orElseThrow(() -> new DomainException("Book not found"));
        Instant now = clock.instant();
        Book updatedBook = Book.create(
                book.id(),
                BookName.of(command.name()).value(),
                List.of(),
                PageCount.of(command.pageCount()).value(),
                book.createdAt(),
                now,
                command.version()
        );
        Book saved = persistBookWithAuthors(
                updatedBook,
                command.authors().stream().distinct().toList(),
                now
        );
        eventPublisher.publishEvent(new BookAuthorsChangedEvent(saved.id(), saved.authors()));
        return saved;
    }

    @Override
    @Transactional
    public Book updateBookName(UpdateBookNameCommand command) {
        Book book = bookStore.findById(BookId.of(command.id()))
                .orElseThrow(() -> new DomainException("Book not found"));
        Book updatedBook = Book.create(
                book.id(),
                BookName.of(command.name()).value(),
                List.of(),
                book.pageCount(),
                book.createdAt(),
                clock.instant(),
                command.version()
        );
        Book savedBook = bookStore.save(updatedBook);
        return enrichWithAuthors(savedBook);
    }

    @Override
    public Page<Book> getBooks(GetBooksQuery query) {
        return bookStore.findAll(Pageable.ofSize(query.size()).withPage(query.page()))
                .map(this::enrichWithAuthors);
    }

    @Override
    public Page<Book> getBooks(Pageable pageable) {
        return bookStore.findAll(pageable)
                .map(this::enrichWithAuthors);
    }

    @Override
    public Optional<Book> getBook(ById<BookId> query) {
        BookId bookId = query.id();
        return bookStore.findById(bookId).map(this::enrichWithAuthors);
    }

    @Override
    public List<Book> getBooks(ByIds<BookId> query) {
        return bookStore.findAllByIds(query.ids()).stream().map(this::enrichWithAuthors).toList();
    }

    private Book persistBookWithAuthors(Book book, List<AuthorId> authorIds, Instant now) {
        Book savedBook = bookStore.save(book);
        bookAuthorLinkStore.replaceAuthorsForBook(savedBook.id(), authorIds.stream().collect(Collectors.toSet()), now);
        return ImmutableBook.builder()
                .from(savedBook)
                .authors(authorIds)
                .build();
    }

    private Book enrichWithAuthors(Book book) {
        List<AuthorId> authors = bookAuthorLinkStore.findAuthorIdsByBookIds(List.of(book.id()))
                .getOrDefault(book.id(), List.of());
        return ImmutableBook.builder()
                .from(book)
                .authors(authors)
                .build();
    }
}
