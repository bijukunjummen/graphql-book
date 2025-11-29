package org.bk.graphql.web.dto;

import org.bk.graphql.domain.Book;

public record BookDto(String id, String name, int pageCount, int version) {
    public static BookDto map(Book book) {
        return new BookDto(book.id().id(), book.name(), book.pageCount(), book.version());
    }
}

