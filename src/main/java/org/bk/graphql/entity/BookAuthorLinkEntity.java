package org.bk.graphql.entity;

import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.BookAuthorLink;
import org.bk.graphql.domain.BookAuthorLinkId;
import org.bk.graphql.domain.BookId;
import org.bk.graphql.domain.ImmutableBookAuthorLink;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("book_author")
public record BookAuthorLinkEntity(
        @Id UUID id,
        UUID bookId,
        UUID authorId,
        Instant createdAt,
        Instant updatedAt) {

    public static BookAuthorLinkEntity fromModel(BookAuthorLink bookAuthor) {
        return new BookAuthorLinkEntity(
                bookAuthor.id().id(),
                bookAuthor.bookId().id(),
                bookAuthor.authorId().id(),
                bookAuthor.createdAt(),
                bookAuthor.updatedAt()
        );
    }

    public BookAuthorLink toModel() {
        return ImmutableBookAuthorLink.builder()
                .id(BookAuthorLinkId.of(id))
                .bookId(BookId.of(bookId))
                .authorId(AuthorId.of(authorId))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
