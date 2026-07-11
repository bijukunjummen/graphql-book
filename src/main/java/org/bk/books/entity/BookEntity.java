package org.bk.books.entity;

import org.bk.books.domain.Book;
import org.bk.books.domain.BookId;
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
        return Book.create(
                BookId.of(id),
                name,
                // Placeholder..filled in by application layer.
                List.of(),
                pageCount,
                createdAt,
                updatedAt,
                version
        );
    }
}
