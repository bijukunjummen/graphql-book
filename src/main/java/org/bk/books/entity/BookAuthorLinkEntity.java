package org.bk.books.entity;

import org.bk.books.domain.AuthorId;
import org.bk.books.domain.BookAuthorLink;
import org.bk.books.domain.BookAuthorLinkId;
import org.bk.books.domain.BookId;
import org.bk.books.domain.ImmutableBookAuthorLink;
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
