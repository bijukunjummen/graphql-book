package org.bk.books.service;

import org.bk.books.common.query.ById;
import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.book.Book;
import org.bk.books.service.author.AuthorService;
import org.bk.books.service.author.AuthorServiceCommands.CreateAuthorCommand;
import org.bk.books.service.book.BookCommands.CreateBookCommand;
import org.bk.books.service.book.BookCommands.UpdateBookAuthorsCommand;
import org.bk.books.service.book.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.test.database.replace=NONE")
@Testcontainers
class BookServiceITest {

    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorService authorService;

    @Test
    void test_setAndReplaceAuthors_success() {
        Author author1 = authorService.createAuthor(new CreateAuthorCommand("author-1"));
        Author author2 = authorService.createAuthor(new CreateAuthorCommand("author-1"));
        Book book = bookService.createBook(new CreateBookCommand("book-1", 100, List.of(author1.id())));
        bookService.updateBookAuthors(new UpdateBookAuthorsCommand(book.id(), List.of(author2.id())));
        assertThat(bookService.getBook(new ById<>(book.id())).orElseThrow().authors()).isEqualTo(List.of(author2.id()));
    }
}
