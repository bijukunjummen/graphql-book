package org.bk.graphql.service;

import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.Book;
import org.bk.graphql.domain.BookId;
import org.bk.graphql.entity.AuthorRef;
import org.bk.graphql.entity.BookEntity;
import org.bk.graphql.repository.BookRepository;
import org.bk.graphql.service.exception.DomainException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final Clock clock;

    public BookServiceImpl(
            BookRepository bookRepository,
            AuthorService authorService,
            Clock clock) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.clock = clock;
    }

    @Override
    public Book createBook(CreateBookCommand command) {
        String bookId = UUID.randomUUID().toString();

        Instant now = clock.instant();
        BookEntity book = new BookEntity(
                bookId,
                command.name(),
                command.pageCount(),
                command.authors().stream()
                        .map(authorId -> new AuthorRef(AggregateReference.to(authorId.id().toString())))
                        .collect(Collectors.toSet()),
                now,
                now,
                0
        );
        BookEntity savedBook = bookRepository.save(book);
        return savedBook.toModel();
    }

    @Override
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
                    command.authors().stream()
                            .map(authorId -> new AuthorRef(AggregateReference.to(authorId.id().toString())))
                            .collect(Collectors.toSet()),
                    existingBook.createdAt(),
                    now,
                    command.version()
            );
            return bookRepository.save(updatedBook).toModel();
        }).orElseGet(() -> {
            BookEntity newBook = new BookEntity(
                    command.id(),
                    command.name(),
                    command.pageCount(),
                    command.authors().stream()
                            .map(authorId -> new AuthorRef(AggregateReference.to(authorId.id().toString())))
                            .collect(Collectors.toSet()),
                    now,
                    now,
                    0
            );
            return bookRepository.save(newBook).toModel();
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
                command.authors().stream()
                        .map(authorId -> new AuthorRef(AggregateReference.to(authorId.id().toString())))
                        .collect(Collectors.toSet()),
                book.createdAt(),
                now,
                command.version()
        );
        BookEntity savedBook = bookRepository.save(updatedBook);
        return savedBook.toModel();
    }

    @Override
    public Book updateBookName(UpdateBookNameCommand command) {
        BookEntity book = bookRepository.findById(command.id())
                .orElseThrow(() -> new DomainException("Book not found"));
        BookEntity updatedBook = new BookEntity(
                book.id(),
                command.name(),
                book.pageCount(),
                book.authors(),
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
        return bookRepository.findById(bookId.id().toString()).map(BookEntity::toModel);
    }

    @Override
    public List<Book> getBooks(ByIds<BookId> query) {
        return StreamSupport.stream(
                bookRepository.findAllById(
                        query.ids().stream().map(BookId::id).map(UUID::toString).toList()
                ).spliterator(), false
        ).map(BookEntity::toModel).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Map<BookId, List<Author>> getAuthorsForBooks(ByIds<BookId> ids) {
        List<Book> booksFromDb = getBooks(ids);
        List<AuthorId> authorIds = booksFromDb.stream()
                .flatMap(book -> book.authors().stream())
                .toList();
        List<Author> authorsFromDb = authorService.getAuthors(new ByIds<>(authorIds));
        Map<AuthorId, Author> authorsById = authorsFromDb.stream()
                .collect(Collectors.toMap(Author::id, a -> a));
        return booksFromDb.stream()
                .collect(Collectors.toMap(
                        Book::id,
                        book -> book.authors().stream()
                                .map(authorsById::get)
                                .toList()
                ));
    }
}
