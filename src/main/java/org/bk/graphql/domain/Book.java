package org.bk.graphql.domain;

import java.time.Instant;
import java.util.List;

public record Book(BookId id, String name, int pageCount, List<AuthorId> authors, Instant createdAt, Instant updatedAt, int version) {
}
