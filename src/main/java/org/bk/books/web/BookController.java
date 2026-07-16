package org.bk.books.web;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bk.books.application.BookAuthorManagementService;
import org.bk.books.common.query.ById;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.Book;
import org.bk.books.domain.entity.book.BookId;
import org.bk.books.service.book.BookCommands.CreateBookCommand;
import org.bk.books.service.book.BookCommands.UpdateBookAuthorsCommand;
import org.bk.books.service.book.BookCommands.UpdateBookNameCommand;
import org.bk.books.web.dto.AuthorDto;
import org.bk.books.web.dto.BookDto;
import org.bk.books.web.dto.CreateBookInput;
import org.bk.books.web.dto.CreateBookPayload;
import org.bk.books.web.dto.SortInput;
import org.bk.books.web.dto.UpdateBookAuthorsInput;
import org.bk.books.web.dto.UpdateBookAuthorsPayload;
import org.bk.books.web.dto.UpdateBookNameInput;
import org.bk.books.web.dto.UpdateBookNamePayload;
import org.bk.books.web.pagination.ConnectionPageSupport;
import org.dataloader.DataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class BookController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);
    private final BookAuthorManagementService bookAuthorManagementService;
    private final ConnectionPageSupport pagination;

    public BookController(BookAuthorManagementService bookAuthorManagementService, ConnectionPageSupport pagination) {
        this.bookAuthorManagementService = bookAuthorManagementService;
        this.pagination = pagination;
    }

    @QueryMapping
    public BookDto findBookById(@Argument UUID id) {
        return BookDto.map(
                bookAuthorManagementService.getBook(new ById<>(new BookId(id))).orElseThrow());
    }

    @MutationMapping
    public CreateBookPayload createBook(@Argument CreateBookInput input) {
        Book createdBook = bookAuthorManagementService.createBook(new CreateBookCommand(
                input.name(),
                input.pageCount(),
                input.authors().stream().map(AuthorId::parse).toList()));
        return new CreateBookPayload(BookDto.map(createdBook));
    }

    @MutationMapping
    public UpdateBookNamePayload updateBookName(@Argument UpdateBookNameInput input) {
        Book updatedBook = bookAuthorManagementService.updateBookName(
                new UpdateBookNameCommand(BookId.parse(input.id()), input.name(), input.version()));
        return new UpdateBookNamePayload(BookDto.map(updatedBook));
    }

    @MutationMapping
    public UpdateBookAuthorsPayload updateBookAuthors(@Argument UpdateBookAuthorsInput input) {
        Book updatedBook = bookAuthorManagementService.updateBookAuthors(new UpdateBookAuthorsCommand(
                BookId.parse(input.id()),
                input.authors().stream().map(AuthorId::parse).toList(),
                input.version()));
        return new UpdateBookAuthorsPayload(BookDto.map(updatedBook));
    }

    @SubscriptionMapping
    public Flux<BookDto> getABook(@Argument BookId id) {
        return Flux.interval(Duration.ofSeconds(5))
                .map(l -> BookDto.map(
                        bookAuthorManagementService.getBook(new ById<>(id)).orElseThrow()));
    }

    //    @BatchMapping(typeName = "Book")
    //    public Map<BookDto, List<AuthorDto>> authors(Set<BookDto> books) {
    //        LOGGER.atInfo().setMessage("Batch Mapping called").log();
    //        List<BookId> bookIds =
    //                books.stream().map(book -> BookId.parse(book.id())).toList();
    //        Map<BookId, List<Author>> authorsForBooks =
    //                bookAuthorManagementService.getAuthorsForBooks(new ByIds<>(bookIds));
    //        return books.stream()
    //                .collect(Collectors.toMap(
    //                        Function.identity(),
    //                        book -> authorsForBooks.getOrDefault(BookId.parse(book.id()), List.of()).stream()
    //                                .map(AuthorDto::map)
    //                                .toList()));
    //    }

    @SchemaMapping(typeName = "Book")
    public CompletableFuture<List<AuthorDto>> authors(
            BookDto bookDto, DataLoader<BookId, BookAuthorsDataLoader.AuthorsWrapper> authorsDataLoader) {
        return authorsDataLoader
                .load(BookId.parse(bookDto.id()))
                .thenApply(BookAuthorsDataLoader.AuthorsWrapper::authors);
    }

    @QueryMapping
    public Page<BookDto> findBooks(ScrollSubrange subrange, @Argument List<SortInput> sort) {
        return pagination.page(
                subrange, sort, pageable -> bookAuthorManagementService.getBooks(pageable), BookDto::map);
    }
}
