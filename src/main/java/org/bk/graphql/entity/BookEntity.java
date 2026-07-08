package org.bk.graphql.entity;

import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.Book;
import org.bk.graphql.domain.BookId;
import org.bk.graphql.domain.ImmutableBook;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Set;

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
        return ImmutableBook.builder()
                .id(BookId.parse(id))
                .name(name)
                .pageCount(pageCount)
                .authors(authors.stream()
                        .map(author -> AuthorId.parse(author.author().getId()))
                        .toList())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .version(version)
                .build();
    }
}
