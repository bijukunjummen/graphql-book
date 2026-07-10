package org.bk.graphql.service.book;

import org.bk.graphql.domain.AuthorId;

import java.util.Set;

public interface BookCommands {
    record CreateBookCommand(String name, int pageCount, Set<AuthorId> authors) {
    }
    record CreateOrUpdateBookCommand(String id, String name, int pageCount, Set<AuthorId> authors, int version) {
        public CreateOrUpdateBookCommand(String id, String name, int pageCount, Set<AuthorId> authors) {
            this(id, name, pageCount, authors, 0);
        }
    }
    record UpdateBookCommand(String id, String name, int pageCount, Set<AuthorId> authors, int version) {
    }
    record UpdateBookNameCommand(String id, String name, int version) {
    }
}
