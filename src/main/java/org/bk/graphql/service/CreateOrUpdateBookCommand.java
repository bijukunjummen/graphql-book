package org.bk.graphql.service;

import org.bk.graphql.domain.AuthorId;

import java.util.Set;

public record CreateOrUpdateBookCommand(String id, String name, int pageCount, Set<AuthorId> authors, int version) {
    public CreateOrUpdateBookCommand(String id, String name, int pageCount, Set<AuthorId> authors) {
        this(id, name, pageCount, authors, 0);
    }
}

