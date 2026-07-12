package org.bk.books.domain.entity.book;

import java.time.Instant;
import org.bk.books.domain.entity.author.AuthorId;
import org.immutables.value.Value;

@Value.Immutable
public interface BookAuthorLink {
  BookAuthorLinkId id();

  BookId bookId();

  AuthorId authorId();

  Instant createdAt();

  Instant updatedAt();
}
