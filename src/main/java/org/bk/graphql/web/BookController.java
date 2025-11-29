package org.bk.graphql.web;

import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.Book;
import org.bk.graphql.domain.BookId;
import org.bk.graphql.service.BookService;
import org.bk.graphql.service.ById;
import org.bk.graphql.service.ByIds;
import org.bk.graphql.service.CreateBookCommand;
import org.bk.graphql.web.dto.AuthorDto;
import org.bk.graphql.web.dto.BookDto;
import org.bk.graphql.web.dto.CreateBookInput;
import org.bk.graphql.web.dto.CreateBookPayload;
import org.bk.graphql.web.dto.OrderField;
import org.bk.graphql.web.dto.SortInput;
import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
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
import java.util.stream.Collectors;

@Controller
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @QueryMapping
    public BookDto findBookById(@Argument String id) {
        return BookDto.map(bookService.getBook(new ById<>(new BookId(id))).orElseThrow());
    }

    @MutationMapping
    public CreateBookPayload createBook(@Argument CreateBookInput input) {
        Book createdBook = bookService.createBook(
            new CreateBookCommand(
                input.name(),
                input.pageCount(),
                input.authors().stream().map(AuthorId::new).collect(Collectors.toSet())
            )
        );
        return new CreateBookPayload(BookDto.map(createdBook));
    }

    @SubscriptionMapping
    public Flux<BookDto> getABook(@Argument String id) {
        return Flux.interval(Duration.ofSeconds(5))
            .map(l -> BookDto.map(bookService.getBook(new ById<>(new BookId(id))).orElseThrow()));
    }

    @BatchMapping(typeName = "Book")
    public Map<BookDto, List<AuthorDto>> authors(Set<BookDto> books) {
        List<BookId> bookIds = books.stream().map(book -> new BookId(book.id())).toList();
        Map<BookId, List<Author>> authorsForBooks = bookService.getAuthorsForBooks(new ByIds<>(bookIds));
        return books.stream()
            .collect(Collectors.toMap(
                book -> book,
                book -> {
                    List<Author> authors = authorsForBooks.get(new BookId(book.id()));
                    return authors != null ? authors.stream().map(AuthorDto::map).toList() : List.of();
                }
            ));
    }

    @QueryMapping
    public Page<BookDto> findBooks(
        ScrollSubrange subrange,
        @Argument List<SortInput> sort
    ) {
        if (sort == null) {
            sort = List.of(new SortInput("name", OrderField.ASC));
        }
        OffsetScrollPosition scrollPosition = (OffsetScrollPosition) subrange.position()
            .orElse(ScrollPosition.offset());
        int limit = subrange.count().orElse(10);
        int offset = scrollPosition.isInitial() ? 0 : (int) (scrollPosition.getOffset() + 1);
        List<Sort.Order> orderList = sort.stream()
            .map(s -> Sort.Order.by(s.field())
                .with(s.order() == OrderField.ASC ? Sort.Direction.ASC : Sort.Direction.DESC))
            .collect(Collectors.toList());
        Sort sortObj = Sort.by(orderList);
        PageRequest pageable = PageRequest.of(limit != 0 ? offset / limit : 0, limit, sortObj);
        Page<Book> page = bookService.getBooks(pageable);
        return page.map(BookDto::map);
    }
}

