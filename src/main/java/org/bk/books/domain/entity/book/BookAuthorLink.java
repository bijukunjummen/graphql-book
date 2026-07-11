package org.bk.books.domain.entity.book;

import org.bk.books.domain.entity.author.AuthorId;
import org.immutables.value.Value;

import java.time.Instant;

@Value.Immutable
public interface BookAuthorLink {
    BookAuthorLinkId id();
    BookId bookId();
    AuthorId authorId();
    Instant createdAt();
    Instant updatedAt();
}
