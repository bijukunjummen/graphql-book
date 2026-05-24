package org.bk.graphql.domain;

import java.time.Instant;

public record Author(AuthorId id, String name, Instant createdAt, Instant updatedAt, int version) {
}
