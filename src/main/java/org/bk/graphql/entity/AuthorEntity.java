package org.bk.graphql.entity;

import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("authors")
public record AuthorEntity(
    @Id String id,
    String name,
    @Version int version
) {
    public Author toModel() {
        return new Author(new AuthorId(id), name, version);
    }
}

