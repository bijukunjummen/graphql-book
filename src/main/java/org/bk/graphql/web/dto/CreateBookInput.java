package org.bk.graphql.web.dto;

import java.util.Set;

public record CreateBookInput(String name, int pageCount, Set<String> authors) {
}

