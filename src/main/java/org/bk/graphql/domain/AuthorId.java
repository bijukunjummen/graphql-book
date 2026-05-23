package org.bk.graphql.domain;

import java.util.UUID;

public record AuthorId(UUID id) {
    public static AuthorId parse(String id) {
        return new AuthorId(UUID.fromString(id));
    }
}

