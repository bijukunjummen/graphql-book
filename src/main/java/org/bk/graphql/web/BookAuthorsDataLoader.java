package org.bk.graphql.web;

import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.BookId;
import org.bk.graphql.service.BookService;
import org.bk.graphql.service.ByIds;
import org.bk.graphql.web.dto.AuthorDto;
import org.dataloader.BatchLoaderEnvironment;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.bk.graphql.web.AuthorsWrapper;

public class BookAuthorsDataLoader implements BiFunction<Set<BookId>, BatchLoaderEnvironment, Mono<Map<BookId, AuthorsWrapper>>> {
    private final BookService bookService;

    public BookAuthorsDataLoader(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public Mono<Map<BookId, AuthorsWrapper>> apply(Set<BookId> bookIds, BatchLoaderEnvironment u) {
        return Mono.fromSupplier(() -> {
            Map<BookId, List<Author>> authorsForBooks = bookService.getAuthorsForBooks(new ByIds<>(bookIds.stream().toList()));
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
}


