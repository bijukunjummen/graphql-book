package org.bk.books.web.dto;

import java.util.Set;

public record CreateBookInput(String name, int pageCount, Set<String> authors) {
}
