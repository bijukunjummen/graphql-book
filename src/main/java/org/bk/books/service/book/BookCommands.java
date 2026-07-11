package org.bk.books.service.book;

import org.bk.books.domain.AuthorId;
import org.bk.books.domain.BookId;

import java.util.List;
import java.util.Set;

public interface BookCommands {
    record CreateBookCommand(String name, int pageCount, Set<AuthorId> authors) {
    }
    record CreateOrUpdateBookCommand(BookId id, String name, int pageCount, Set<AuthorId> authors, int version) {
        public CreateOrUpdateBookCommand(BookId id, String name, int pageCount, Set<AuthorId> authors) {
            this(id, name, pageCount, authors, 0);
        }
    }
    record UpdateBookCommand(BookId id, String name, int pageCount, List<AuthorId> authors, int version) {
    }
    record UpdateBookNameCommand(BookId id, String name, int version) {
    }
    record UpdateBookAuthorsCommand(BookId id, List<AuthorId> authorIds) {}
}
