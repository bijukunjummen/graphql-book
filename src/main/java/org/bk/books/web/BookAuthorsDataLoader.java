package org.bk.books.web;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.bk.books.application.BookAuthorManagementService;
import org.bk.books.common.query.ByIds;
import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.book.BookId;
import org.bk.books.web.dto.AuthorDto;
import org.dataloader.BatchLoaderEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class BookAuthorsDataLoader
        implements BiFunction<
                Set<BookId>, BatchLoaderEnvironment, Mono<Map<BookId, BookAuthorsDataLoader.AuthorsWrapper>>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookAuthorsDataLoader.class);
    private final BookAuthorManagementService bookAuthorManagementService;

    public BookAuthorsDataLoader(BookAuthorManagementService bookAuthorManagementService) {
        this.bookAuthorManagementService = bookAuthorManagementService;
    }

    @Override
    public Mono<Map<BookId, AuthorsWrapper>> apply(Set<BookId> bookIds, BatchLoaderEnvironment u) {
        return Mono.fromSupplier(() -> {
            LOGGER.atInfo().setMessage("Dataloader called..").log();
            Map<BookId, List<Author>> authorsForBooks = bookAuthorManagementService.getAuthorsForBooks(
                    new ByIds<>(bookIds.stream().toList()));
            return bookIds.stream()
                    .collect(toMap(
                            Function.identity(),
                            bookId -> new AuthorsWrapper(authorsForBooks.getOrDefault(bookId, List.of()).stream()
                                    .map(AuthorDto::map)
                                    .toList())));
        });
    }

    public record AuthorsWrapper(List<AuthorDto> authors) {}
}
