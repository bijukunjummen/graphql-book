package org.bk.books.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.bk.books.application.BookAuthorManagementService;
import org.bk.books.common.query.ById;
import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.book.Book;
import org.bk.books.service.author.AuthorService;
import org.bk.books.service.author.AuthorServiceCommands.CreateAuthorCommand;
import org.bk.books.service.book.BookCommands.CreateBookCommand;
import org.bk.books.service.book.BookCommands.UpdateBookAuthorsCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class BookAuthorManagementServiceITest {

    @Autowired
    private BookAuthorManagementService bookAuthorManagementService;

    @Autowired
    private AuthorService authorService;

    @Test
    void test_setAndReplaceAuthors_success() {
        Author author1 = authorService.createAuthor(new CreateAuthorCommand("author-1"));
        Author author2 = authorService.createAuthor(new CreateAuthorCommand("author-2"));
        Book book = bookAuthorManagementService.createBook(new CreateBookCommand("book-1", 100, List.of(author1.id())));
        bookAuthorManagementService.updateBookAuthors(
                new UpdateBookAuthorsCommand(book.id(), List.of(author2.id()), book.version()));
        assertThat(bookAuthorManagementService
                        .getBook(new ById<>(book.id()))
                        .orElseThrow()
                        .authors())
                .isEqualTo(List.of(author2.id()));
    }

    //    @DynamicPropertySource
    //    static void dynamicProperties(DynamicPropertyRegistry registry) {
    //        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    //        registry.add("spring.datasource.username", postgresContainer::getUsername);
    //        registry.add("spring.datasource.password", postgresContainer::getPassword);
    //    }
    //
    //    @TestConfiguration
    //    static class BookServiceTestConfiguration {
    //
    //        @Bean
    //        public JdbcConnectionDetails jdbcConnectionDetails() {
    //            return new JdbcConnectionDetails() {
    //                @Override
    //                public @Nullable String getUsername() {
    //                    return postgresContainer.getUsername();
    //                }
    //
    //                @Override
    //                public @Nullable String getPassword() {
    //                    return postgresContainer.getPassword();
    //                }
    //
    //                @Override
    //                public String getJdbcUrl() {
    //                    return postgresContainer.getJdbcUrl();
    //                }
    //            };
    //        }
    //    }
    @ServiceConnection
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.5-bullseye");
}
