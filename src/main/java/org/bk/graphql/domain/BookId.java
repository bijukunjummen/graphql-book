package org.bk.graphql.domain;

import java.util.UUID;

public record BookId(UUID id) {
    public static BookId parse(String id) {
        return new BookId(UUID.fromString(id));
    }

    public static BookId of(UUID id) {
        return new BookId(id);
    }
}

