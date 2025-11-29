package org.bk.graphql.service;

import org.bk.graphql.domain.AuthorId;

import java.util.Set;

public record UpdateBookCommand(String id, String name, int pageCount, Set<AuthorId> authors, int version) {
}

