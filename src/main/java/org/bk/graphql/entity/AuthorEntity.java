package org.bk.graphql.entity;

import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("authors")
public record AuthorEntity(
    @Id String id,
    String name,
    Instant createdAt,
    Instant updatedAt,
    @Version int version
) {
    public Author toModel() {
        return new Author(AuthorId.parse(id), name, createdAt, updatedAt, version);
    }
}
