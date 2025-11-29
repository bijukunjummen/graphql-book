package org.bk.graphql.service;

import org.bk.graphql.domain.AuthorId;

import java.util.Set;

public record CreateBookCommand(String name, int pageCount, Set<AuthorId> authors) {
}

