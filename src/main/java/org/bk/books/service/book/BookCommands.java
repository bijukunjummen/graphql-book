package org.bk.books.service.book;

import org.bk.books.domain.AuthorId;

import java.util.Set;
import java.util.UUID;

public interface BookCommands {
    record CreateBookCommand(String name, int pageCount, Set<AuthorId> authors) {
    }
    record CreateOrUpdateBookCommand(UUID id, String name, int pageCount, Set<AuthorId> authors, int version) {
        public CreateOrUpdateBookCommand(UUID id, String name, int pageCount, Set<AuthorId> authors) {
            this(id, name, pageCount, authors, 0);
        }
    }
    record UpdateBookCommand(UUID id, String name, int pageCount, Set<AuthorId> authors, int version) {
    }
    record UpdateBookNameCommand(UUID id, String name, int version) {
    }
}
