package org.bk.books.domain;

import java.util.UUID;

public record AuthorId(UUID id) {
    public static AuthorId parse(String id) {
        return new AuthorId(UUID.fromString(id));
    }

    public static AuthorId of(UUID id) {
        return new AuthorId(id);
    }
}

