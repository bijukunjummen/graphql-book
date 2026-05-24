package org.bk.graphql.domain;

import org.immutables.value.Value;

import java.time.Instant;
import java.util.List;

@Value.Immutable
public interface Book {
    BookId id();
    String name();
    int pageCount();
    List<AuthorId> authors();
    Instant createdAt();
    Instant updatedAt();
    int version();
}
