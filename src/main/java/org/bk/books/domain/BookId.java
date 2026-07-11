package org.bk.books.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.UUID;

public record BookId(UUID id) {

    @JsonCreator
    public static BookId parse(String id) {
        return new BookId(UUID.fromString(id));
    }

    public static BookId of(UUID id) {
        return new BookId(id);
    }
}

