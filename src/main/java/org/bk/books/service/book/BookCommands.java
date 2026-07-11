package org.bk.books.service.book;

import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.BookId;

import java.util.List;

public interface BookCommands {
    record CreateBookCommand(String name, int pageCount, List<AuthorId> authors) {
    }
    record CreateOrUpdateBookCommand(BookId id, String name, int pageCount, List<AuthorId> authors, int version) {
        public CreateOrUpdateBookCommand(BookId id, String name, int pageCount, List<AuthorId> authors) {
            this(id, name, pageCount, authors, 0);
        }
    }
    record UpdateBookCommand(BookId id, String name, int pageCount, List<AuthorId> authors, int version) {
    }
    record UpdateBookNameCommand(BookId id, String name, int version) {
    }
    record UpdateBookAuthorsCommand(BookId id, List<AuthorId> authorIds) {}
}
