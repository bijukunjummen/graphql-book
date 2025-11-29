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

    public BookServiceImpl(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    @Override
    public Book createBook(CreateBookCommand command) {
        String bookId = UUID.randomUUID().toString();

        BookEntity book = new BookEntity(
            bookId,
            command.name(),
            command.pageCount(),
            command.authors().stream()
                .map(authorId -> new AuthorRef(AggregateReference.to(authorId.id())))
                .collect(Collectors.toSet()),
            0
        );
        BookEntity savedBook = bookRepository.save(book);
        return savedBook.toModel();
    }

    @Override
    public Book createOrUpdateBook(CreateOrUpdateBookCommand command) {
        var book = bookRepository.findById(command.id());

        if (book.isPresent()) {
            if (command.version() != 0) {
                BookEntity updatedBook = new BookEntity(
                    book.get().id(),
                    command.name(),
                    command.pageCount(),
                    command.authors().stream()
                        .map(authorId -> new AuthorRef(AggregateReference.to(authorId.id())))
                        .collect(Collectors.toSet()),
                    command.version()
                );
                bookRepository.save(updatedBook);
            }
        } else {
            BookEntity newBook = new BookEntity(
                command.id(),
                command.name(),
                command.pageCount(),
                command.authors().stream()
                    .map(authorId -> new AuthorRef(AggregateReference.to(authorId.id())))
                    .collect(Collectors.toSet()),
                0
            );
            bookRepository.save(newBook);
        }
        return bookRepository.findById(command.id()).orElseThrow().toModel();
    }

    @Override
    public Book updateBook(UpdateBookCommand command) {
        BookEntity book = bookRepository.findById(command.id())
            .orElseThrow(() -> new DomainException("Book not found"));
        BookEntity updatedBook = new BookEntity(
            book.id(),
            command.name(),
            command.pageCount(),
            command.authors().stream()
                .map(authorId -> new AuthorRef(AggregateReference.to(authorId.id())))
                .collect(Collectors.toSet()),
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

