package org.bk.books.web;

import org.bk.books.application.BookAuthorManagementService;
import org.bk.books.common.query.ByIds;
import org.bk.books.domain.Author;
import org.bk.books.domain.BookId;
import org.bk.books.web.dto.AuthorDto;
import org.dataloader.BatchLoaderEnvironment;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class BookAuthorsDataLoader implements BiFunction<Set<BookId>, BatchLoaderEnvironment, Mono<Map<BookId, BookAuthorsDataLoader.AuthorsWrapper>>> {
    private final BookAuthorManagementService bookAuthorManagementService;

    public BookAuthorsDataLoader(BookAuthorManagementService bookAuthorManagementService) {
        this.bookAuthorManagementService = bookAuthorManagementService;
    }

    @Override
    public Mono<Map<BookId, AuthorsWrapper>> apply(Set<BookId> bookIds, BatchLoaderEnvironment u) {
        return Mono.fromSupplier(() -> {
            Map<BookId, List<Author>> authorsForBooks = bookAuthorManagementService.getAuthorsForBooks(new ByIds<>(bookIds.stream().toList()));
            return authorsForBooks.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> {
                        List<AuthorDto> authorDtos = entry.getValue().stream()
                            .map(AuthorDto::map)
                            .toList();
                        return new AuthorsWrapper(authorDtos);
                    }
                ));
        });
    }
    public record AuthorsWrapper(List<AuthorDto> authors) {
    }
}


