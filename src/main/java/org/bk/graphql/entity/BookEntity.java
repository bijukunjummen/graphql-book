package org.bk.graphql.entity;

import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.Book;
import org.bk.graphql.domain.BookId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Table("books")
public record BookEntity(
        @Id String id,
        String name,
        int pageCount,
        @MappedCollection(idColumn = "book_id") Set<AuthorRef> authors,
        Instant createdAt,
        Instant updatedAt,
        @Version int version
) {
    public Book toModel() {
        return new Book(
                BookId.parse(id),
                name,
                pageCount,
                authors.stream()
                        .map(author -> AuthorId.parse(author.author().getId()))
                        .toList(),
                createdAt,
                updatedAt,
                version
        );
    }
}
