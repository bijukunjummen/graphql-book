package org.bk.books.web;

import org.bk.books.application.BookAuthorManagementService;
import org.bk.books.common.query.ById;
import org.bk.books.common.query.ByIds;
import org.bk.books.domain.Author;
import org.bk.books.domain.AuthorId;
import org.bk.books.domain.Book;
import org.bk.books.domain.BookId;
import org.bk.books.service.book.BookCommands.CreateBookCommand;
import org.bk.books.service.book.BookCommands.UpdateBookNameCommand;
import org.bk.books.service.book.BookService;
import org.bk.books.web.dto.AuthorDto;
import org.bk.books.web.dto.BookDto;
import org.bk.books.web.dto.CreateBookInput;
import org.bk.books.web.dto.CreateBookPayload;
import org.bk.books.web.dto.SortInput;
import org.bk.books.web.dto.UpdateBookNameInput;
import org.bk.books.web.dto.UpdateBookNamePayload;
import org.bk.books.web.pagination.ConnectionPageSupport;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class BookController {
    private final BookService bookService;
    private final BookAuthorManagementService bookAuthorManagementService;
    private final ConnectionPageSupport pagination;

    public BookController(
            BookService bookService,
            BookAuthorManagementService bookAuthorManagementService,
            ConnectionPageSupport pagination) {
        this.bookService = bookService;
        this.bookAuthorManagementService = bookAuthorManagementService;
        this.pagination = pagination;
    }

    @QueryMapping
    public BookDto findBookById(@Argument UUID id) {
        return BookDto.map(bookService.getBook(new ById<>(new BookId(id))).orElseThrow());
    }

    @MutationMapping
    public CreateBookPayload createBook(@Argument CreateBookInput input) {
        Book createdBook = bookService.createBook(
                new CreateBookCommand(
                        input.name(),
                        input.pageCount(),
                        input.authors().stream().map(AuthorId::parse).collect(Collectors.toSet())
                )
        );
        return new CreateBookPayload(BookDto.map(createdBook));
    }

    @MutationMapping
    public UpdateBookNamePayload updateBookName(@Argument UpdateBookNameInput input) {
        Book updatedBook = bookService.updateBookName(
                new UpdateBookNameCommand(input.id(), input.name(), input.version())
        );
        return new UpdateBookNamePayload(BookDto.map(updatedBook));
    }

    @SubscriptionMapping
    public Flux<BookDto> getABook(@Argument BookId id) {
        return Flux.interval(Duration.ofSeconds(5))
                .map(l -> BookDto.map(bookService.getBook(new ById<>(id)).orElseThrow()));
    }

    @BatchMapping(typeName = "Book")
    public Map<BookDto, List<AuthorDto>> authors(Set<BookDto> books) {
        List<BookId> bookIds = books.stream().map(book -> BookId.parse(book.id())).toList();
        Map<BookId, List<Author>> authorsForBooks = bookAuthorManagementService.getAuthorsForBooks(new ByIds<>(bookIds));
        return books.stream()
                .collect(Collectors.toMap(
                        book -> book,
                        book -> {
                            List<Author> authors = authorsForBooks.get(BookId.parse(book.id()));
                            return authors != null ? authors.stream().map(AuthorDto::map).toList() : List.of();
                        }
                ));
    }

    @QueryMapping
    public Page<BookDto> findBooks(
            ScrollSubrange subrange,
            @Argument List<SortInput> sort
    ) {
        return pagination.page(subrange, sort, pageable -> bookService.getBooks(pageable), BookDto::map);
    }
}
