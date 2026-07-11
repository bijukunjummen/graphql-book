package org.bk.graphql.entity;

import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.ImmutableAuthor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("authors")
public record AuthorEntity(
    @Id UUID id,
    String name,
    Instant createdAt,
    Instant updatedAt,
    @Version int version
) {
    public Author toModel() {
        return ImmutableAuthor.builder()
                .id(AuthorId.of(id))
                .name(name)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .version(version)
                .build();
    }

    public static AuthorEntity fromModel(Author author) {
        return new AuthorEntity(
                author.id().id(),
                author.name(),
                author.createdAt(),
                author.updatedAt(),
                author.version()
        );
    }

}
