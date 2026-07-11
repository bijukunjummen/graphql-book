package org.bk.books.service.book;

public interface BookQueries {
    record GetBooksQuery(int page, int size) {
    }
}

