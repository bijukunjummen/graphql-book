package org.bk.books.entity;

import java.time.Instant;
import java.util.UUID;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.BookAuthorLink;
import org.bk.books.domain.entity.book.BookAuthorLinkId;
import org.bk.books.domain.entity.book.BookId;
import org.bk.books.domain.entity.book.ImmutableBookAuthorLink;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("book_author")
public record BookAuthorLinkEntity(
    @Id UUID id, UUID bookId, UUID authorId, Instant createdAt, Instant updatedAt) {

  public static BookAuthorLinkEntity fromModel(BookAuthorLink bookAuthor) {
    return new BookAuthorLinkEntity(
        bookAuthor.id().id(),
        bookAuthor.bookId().id(),
        bookAuthor.authorId().id(),
        bookAuthor.createdAt(),
        bookAuthor.updatedAt());
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
