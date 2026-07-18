package org.bk.books.application;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bk.books.common.query.ById;
import org.bk.books.common.query.ByIds;
import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.Book;
import org.bk.books.domain.entity.book.BookEvents.BookAuthorsUpdatedEvent;
import org.bk.books.domain.entity.book.BookId;
import org.bk.books.domain.entity.book.ImmutableBook;
import org.bk.books.port.BookAuthorLinkStore;
import org.bk.books.service.author.AuthorService;
import org.bk.books.service.book.BookCommands;
import org.bk.books.service.book.BookQueries;
import org.bk.books.service.book.BookService;
import org.bk.books.util.Uuids;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("bookManagementService")
public class BookAuthorManagementServiceImpl implements BookAuthorManagementService {
    private final BookService bookService;
    private final BookAuthorLinkStore bookAuthorLinkStore;
    private final AuthorService authorService;
    private final Clock clock;
    private final Uuids uuids;
    private final ApplicationEventPublisher eventPublisher;

    public BookAuthorManagementServiceImpl(
            BookService bookService,
            BookAuthorLinkStore bookAuthorLinkStore,
            AuthorService authorService,
            Clock clock,
            Uuids uuids,
            ApplicationEventPublisher eventPublisher) {
        this.bookService = bookService;
        this.bookAuthorLinkStore = bookAuthorLinkStore;
        this.authorService = authorService;
        this.clock = clock;
        this.uuids = uuids;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Map<BookId, List<Author>> getAuthorsForBooks(ByIds<BookId> ids) {
        Map<BookId, List<AuthorId>> authorIdsForBooks = getAuthorIdsForBooks(ids);
        List<AuthorId> authorIds = authorIdsForBooks.values().stream()
                .flatMap(List::stream)
                .distinct()
                .toList();
        if (authorIds.isEmpty()) {
            return ids.ids().stream().collect(Collectors.toMap(bookId -> bookId, bookId -> List.of()));
        }
        List<Author> authorsFromDb = authorService.getAuthors(new ByIds<>(authorIds));
        Map<AuthorId, Author> authorsById = authorsFromDb.stream().collect(Collectors.toMap(Author::id, a -> a));

        return ids.ids().stream()
                .collect(Collectors.toMap(
                        bookId -> bookId, bookId -> authorIdsForBooks.getOrDefault(bookId, List.of()).stream()
                                .map(authorsById::get)
                                .filter(Objects::nonNull)
                                .toList()));
    }

    @Override
    @Transactional
    public Book createBook(BookCommands.CreateBookCommand command) {
        Book savedBook = bookService.createBook(command);
        return persistAuthorLink(savedBook, command.authors(), savedBook.updatedAt());
    }

    @Override
    @Transactional
    public Book createOrUpdateBook(BookCommands.CreateOrUpdateBookCommand command) {
        Book savedBook = bookService.createOrUpdateBook(command);
        return persistAuthorLink(savedBook, command.authors(), savedBook.updatedAt());
    }

    @Override
    @Transactional
    public Book updateBook(BookCommands.UpdateBookCommand command) {
        Book updatedBook = bookService.updateBook(command);
        return persistAuthorLink(updatedBook, command.authors(), updatedBook.updatedAt());
    }

    @Override
    @Transactional
    public Book updateBookName(BookCommands.UpdateBookNameCommand command) {
        return bookService.updateBookName(command);
    }

    @Override
    @Transactional
    public Book updateBookAuthors(BookCommands.UpdateBookAuthorsCommand command) {
        Book book = getBook(new ById<>(command.id())).orElseThrow();
        Book existingWithAuthors = enrichWithAuthors(book);
        bookAuthorLinkStore.replaceAuthorsForBook(command.id(), command.authorIds(), clock.instant());
        eventPublisher.publishEvent(new BookAuthorsUpdatedEvent(
                uuids.generateUuid(), book.id(), existingWithAuthors.authors(), command.authorIds()));
        return enrichWithAuthors(book);
    }

    @Override
    public Page<Book> getBooks(BookQueries.GetBooksQuery query) {
        Page<Book> booksNoAuthors = bookService.getBooks(query);
        Map<BookId, List<AuthorId>> authorIdsByBookIds = bookAuthorLinkStore.findAuthorIdsByBookIds(
                booksNoAuthors.stream().map(Book::id).toList());
        return booksNoAuthors.map(bookNoAuthor -> ImmutableBook.builder()
                .from(bookNoAuthor)
                .authors(authorIdsByBookIds.getOrDefault(bookNoAuthor, List.of()))
                .build());
    }

    @Override
    public Page<Book> getBooks(Pageable pageable) {
        Page<Book> booksNoAuthors = bookService.getBooks(pageable);
        Map<BookId, List<AuthorId>> authorIdsByBookIds = bookAuthorLinkStore.findAuthorIdsByBookIds(
                booksNoAuthors.stream().map(Book::id).toList());
        return booksNoAuthors.map(bookNoAuthor -> ImmutableBook.builder()
                .from(bookNoAuthor)
                .authors(authorIdsByBookIds.getOrDefault(bookNoAuthor, List.of()))
                .build());
    }

    @Override
    public Optional<Book> getBook(ById<BookId> query) {
        return bookService.getBook(query).map(book -> enrichWithAuthors(book));
    }

    @Override
    public List<Book> getBooks(ByIds<BookId> query) {
        List<Book> books = bookService.getBooks(query);
        Map<BookId, List<AuthorId>> authorIdsByBookIds = bookAuthorLinkStore.findAuthorIdsByBookIds(
                books.stream().map(Book::id).toList());
        return books.stream()
                .<Book>map(bookNoAuthor -> ImmutableBook.builder()
                        .from(bookNoAuthor)
                        .authors(authorIdsByBookIds.getOrDefault(bookNoAuthor, List.of()))
                        .build())
                .toList();
    }

    @Override
    public Map<BookId, List<AuthorId>> getAuthorIdsForBooks(ByIds<BookId> query) {
        return bookAuthorLinkStore.findAuthorIdsByBookIds(query.ids());
    }

    private Book persistAuthorLink(Book book, List<AuthorId> authorIds, Instant now) {
        bookAuthorLinkStore.replaceAuthorsForBook(book.id(), authorIds, now);
        return ImmutableBook.builder().from(book).authors(authorIds).build();
    }

    private Book enrichWithAuthors(Book book) {
        List<AuthorId> authors =
                bookAuthorLinkStore.findAuthorIdsByBookIds(List.of(book.id())).getOrDefault(book.id(), List.of());
        return ImmutableBook.builder().from(book).authors(authors).build();
    }
}
