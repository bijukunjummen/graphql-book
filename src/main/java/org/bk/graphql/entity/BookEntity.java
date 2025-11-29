package org.bk.graphql.entity;

import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.Book;
import org.bk.graphql.domain.BookId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;
import java.util.stream.Collectors;

@Table("books")
public record BookEntity(
    @Id String id,
    String name,
    int pageCount,
    @MappedCollection(idColumn = "book_id") Set<AuthorRef> authors,
    @Version int version
) {
    public Book toModel() {
        return new Book(
            new BookId(id),
            name,
            pageCount,
            authors.stream()
                .map(author -> new AuthorId(author.author().getId()))
                .collect(Collectors.toList()),
            version
        );
    }
}

