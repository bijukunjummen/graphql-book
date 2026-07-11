package org.bk.books.web.dto;

import org.bk.books.domain.Book;


public record BookDto(String id, String name, int pageCount, int version) {
    public static BookDto map(Book book) {
        return new BookDto(book.id().id().toString(), book.name(), book.pageCount(), book.version());
    }
}
