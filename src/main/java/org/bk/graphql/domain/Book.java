package org.bk.graphql.domain;

import java.util.List;

public record Book(BookId id, String name, int pageCount, List<AuthorId> authors, int version) {
}

