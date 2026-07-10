package org.bk.graphql.domain;

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
