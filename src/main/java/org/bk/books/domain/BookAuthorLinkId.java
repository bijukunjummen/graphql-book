package org.bk.books.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.UUID;

public record BookAuthorLinkId(UUID id) {
    @JsonCreator
    public static BookAuthorLinkId parse(String id) {
        return new BookAuthorLinkId(UUID.fromString(id));
    }

    public static BookAuthorLinkId of(UUID id) {
        return new BookAuthorLinkId(id);
    }
}
