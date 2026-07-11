package org.bk.graphql.domain;

import org.bk.graphql.domain.validation.BookName;
import org.bk.graphql.domain.validation.PageCount;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.List;

@Value.Immutable
public interface Book {
    static Book create(
            BookId id,
            String name,
            List<AuthorId> authors,
            int pageCount,
            Instant createdAt,
            Instant updatedAt,
            int version
    ) {
        return ImmutableBook.builder()
                .id(id)
                .name(BookName.of(name).value())
                .authors(authors.stream().distinct().toList())
                .pageCount(PageCount.of(pageCount).value())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .version(version)
                .build();
    }

    BookId id();
    String name();
    List<AuthorId> authors();
    int pageCount();
    Instant createdAt();
    Instant updatedAt();
    int version();
}
