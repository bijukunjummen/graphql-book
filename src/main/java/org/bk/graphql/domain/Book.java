package org.bk.graphql.domain;

import org.immutables.value.Value;

import java.time.Instant;
import java.util.List;

@Value.Immutable
public interface Book {
    BookId id();
    String name();
    List<AuthorId> authors();
    int pageCount();
    Instant createdAt();
    Instant updatedAt();
    int version();
}
