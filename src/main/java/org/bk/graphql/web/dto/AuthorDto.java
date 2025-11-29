package org.bk.graphql.web.dto;

import org.bk.graphql.domain.Author;

public record AuthorDto(String id, String name, int version) {
    public static AuthorDto map(Author author) {
        return new AuthorDto(author.id().id(), author.name(), author.version());
    }
}

