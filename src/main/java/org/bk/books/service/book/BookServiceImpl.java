package org.bk.books.service.book;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.bk.books.common.query.ById;
import org.bk.books.common.query.ByIds;
import org.bk.books.domain.entity.book.Book;
import org.bk.books.domain.entity.book.BookId;
import org.bk.books.domain.event.BookCreatedEvent;
import org.bk.books.domain.event.BookNameUpdatedEvent;
import org.bk.books.domain.event.BookUpdatedEvent;
import org.bk.books.domain.validation.BookName;
import org.bk.books.domain.validation.PageCount;
import org.bk.books.port.BookStore;
import org.bk.books.service.book.BookCommands.CreateBookCommand;
import org.bk.books.service.book.BookCommands.CreateOrUpdateBookCommand;
import org.bk.books.service.book.BookCommands.UpdateBookCommand;
import org.bk.books.service.book.BookCommands.UpdateBookNameCommand;
import org.bk.books.service.book.BookQueries.GetBooksQuery;
import org.bk.books.service.exception.DomainException;
import org.bk.books.util.Uuids;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("bookService")
public class BookServiceImpl implements BookService {
    private final BookStore bookStore;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;
    private final Uuids uuids;

    public BookServiceImpl(BookStore bookStore, ApplicationEventPublisher eventPublisher, Clock clock, Uuids uuids) {
        this.bookStore = bookStore;
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
                0);
        bookStore.save(newBook);
        eventPublisher.publishEvent(
                new BookCreatedEvent(newBook.id(), newBook.name(), newBook.pageCount(), newBook.authors()));
        return newBook;
    }

    @Override
    @Transactional
    public Book createOrUpdateBook(CreateOrUpdateBookCommand command) {
        Optional<Book> book = bookStore.findById(command.id());

        Instant now = clock.instant();
        return book.map(existingBook -> {
                    if (command.version() == 0) {
                        return existingBook;
                    }
                    Book updatedBook = Book.create(
                            existingBook.id(),
                            BookName.of(command.name()).value(),
                            List.of(),
                            PageCount.of(command.pageCount()).value(),
                            existingBook.createdAt(),
                            now,
                            command.version());
                    Book saved = bookStore.save(updatedBook);
                    eventPublisher.publishEvent(
                            new BookUpdatedEvent(saved.id(), saved.name(), saved.pageCount(), saved.authors()));
                    return saved;
                })
                .orElseGet(() -> {
                    Book newBook = Book.create(
                            command.id(),
                            BookName.of(command.name()).value(),
                            List.of(),
                            PageCount.of(command.pageCount()).value(),
                            now,
                            now,
                            0);
                    Book saved = bookStore.save(newBook);
                    eventPublisher.publishEvent(
                            new BookCreatedEvent(saved.id(), saved.name(), saved.pageCount(), saved.authors()));
                    return saved;
                });
    }

    @Override
    @Transactional
    public Book updateBook(UpdateBookCommand command) {
        Book book = bookStore.findById(command.id()).orElseThrow(() -> new DomainException("Book not found"));
        Instant now = clock.instant();
        Book updatedBook = Book.create(
                book.id(),
                BookName.of(command.name()).value(),
                List.of(),
                PageCount.of(command.pageCount()).value(),
                book.createdAt(),
                now,
                command.version());
        bookStore.save(updatedBook);
        eventPublisher.publishEvent(new BookUpdatedEvent(
                updatedBook.id(), updatedBook.name(), updatedBook.pageCount(), updatedBook.authors()));
        return updatedBook;
    }

    @Override
    @Transactional
    public Book updateBookName(UpdateBookNameCommand command) {
        Book book = bookStore.findById(command.id()).orElseThrow(() -> new DomainException("Book not found"));
        Book updatedBook = Book.create(
                book.id(),
                BookName.of(command.name()).value(),
                List.of(),
                book.pageCount(),
                book.createdAt(),
                clock.instant(),
                command.version());
        Book savedBook = bookStore.save(updatedBook);
        eventPublisher.publishEvent(new BookNameUpdatedEvent(savedBook.id(), book.name(), savedBook.name()));
        return savedBook;
    }

    @Override
    public Page<Book> getBooks(GetBooksQuery query) {
        return bookStore.findAll(Pageable.ofSize(query.size()).withPage(query.page()));
    }

    @Override
    public Page<Book> getBooks(Pageable pageable) {
        return bookStore.findAll(pageable);
    }

    @Override
    public Optional<Book> getBook(ById<BookId> query) {
        BookId bookId = query.id();
        return bookStore.findById(bookId);
    }

    @Override
    public List<Book> getBooks(ByIds<BookId> query) {
        return bookStore.findAllByIds(query.ids());
    }
}
