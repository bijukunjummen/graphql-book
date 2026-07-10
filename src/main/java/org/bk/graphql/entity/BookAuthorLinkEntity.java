package org.bk.graphql.entity;

import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.BookAuthorLink;
import org.bk.graphql.domain.BookAuthorLinkId;
import org.bk.graphql.domain.BookId;
import org.bk.graphql.domain.ImmutableBookAuthorLink;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("book_author")
public record BookAuthorLinkEntity(
        @Id String id,
        String bookId,
        String authorId,
        Instant createdAt,
        Instant updatedAt) {

    public static BookAuthorLinkEntity fromModel(BookAuthorLink bookAuthor) {
        return new BookAuthorLinkEntity(
                bookAuthor.id().toString(),
                bookAuthor.bookId().id().toString(),
                bookAuthor.authorId().id().toString(),
                bookAuthor.createdAt(),
                bookAuthor.updatedAt()
        );
    }

    public BookAuthorLink toModel() {
        return ImmutableBookAuthorLink.builder()
                .id(BookAuthorLinkId.parse(id))
                .bookId(BookId.parse(bookId))
                .authorId(AuthorId.parse(authorId))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
