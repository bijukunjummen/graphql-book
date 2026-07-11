package org.bk.graphql.entity;

import org.bk.graphql.domain.Book;
import org.bk.graphql.domain.BookId;
import org.bk.graphql.domain.ImmutableBook;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Table("books")
public record BookEntity(
        @Id UUID id,
        String name,
        int pageCount,
        Instant createdAt,
        Instant updatedAt,
        @Version int version
) {
    public static BookEntity fromModel(Book book) {
        return new BookEntity(book.id().id(), book.name(), book.pageCount(), book.createdAt(), book.updatedAt(), book.version());
    }

    public Book toModel() {
        return ImmutableBook.builder()
                .id(BookId.of(id))
                .name(name)
                .pageCount(pageCount)
                //Placeholder..will be filled in later.
                .authors(List.of())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .version(version)
                .build();
    }
}
