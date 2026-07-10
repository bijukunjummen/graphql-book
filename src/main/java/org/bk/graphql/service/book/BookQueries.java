package org.bk.graphql.service.book;

public interface BookQueries {
    record GetBooksQuery(int page, int size) {
    }
}

