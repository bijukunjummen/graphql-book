package org.bk.graphql.repository;

import org.bk.graphql.AuthorTestData;
import org.bk.graphql.BookAuthorLinkTestData;
import org.bk.graphql.BookTestData;
import org.bk.graphql.entity.AuthorEntity;
import org.bk.graphql.entity.BookAuthorLinkEntity;
import org.bk.graphql.entity.BookEntity;
import org.bk.graphql.repository.author.AuthorRepository;
import org.bk.graphql.repository.book.BookRepository;
import org.bk.graphql.repository.bookauthorlink.BookAuthorLinkRepository;
import org.bk.graphql.service.bookauthorlink.BookAuthorLinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bk.graphql.TimeTestData.DEFAULT_CREATED_DATE;
import static org.bk.graphql.TimeTestData.DEFAULT_UPDATED_DATE;

@DataJdbcTest(properties = "spring.test.database.replace=NONE")
@Testcontainers
class BookRepositoryITest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookAuthorLinkRepository bookAuthorLinkRepository;

    @Test
    void test_crudOperations_withBookAndAuthor_returnsSavedBook() {
        AuthorEntity author = AuthorEntity.fromModel(AuthorTestData.sampleAuthor_1());
        authorRepository.save(author);

        BookEntity book = BookEntity.fromModel(BookTestData.sampleBook());
        bookRepository.save(book);
        bookAuthorLinkRepository.save(BookAuthorLinkEntity.fromModel(BookAuthorLinkTestData.sampleBookAuthorLink()));
        assertThat(bookRepository.findById(book.id()).orElseThrow().name()).isEqualTo(book.name());
    }

    @Test
    void test_getBooks_returnsPaginatedResults() {
        AuthorEntity author1 = AuthorEntity.fromModel(AuthorTestData.sampleAuthor_1());
        AuthorEntity savedAuthor = authorRepository.save(author1);

        for (int i = 0; i < 10; i++) {
            BookEntity book = new BookEntity(UUID.randomUUID(), "name-" + i, 100, DEFAULT_CREATED_DATE, DEFAULT_UPDATED_DATE, 0);
            bookRepository.save(book);
            bookAuthorLinkRepository.save(new BookAuthorLinkEntity(UUID.randomUUID(), book.id(), savedAuthor.id(), DEFAULT_CREATED_DATE, DEFAULT_UPDATED_DATE));
        }

        Pageable page1 = PageRequest.of(0, 2);

        Page<BookEntity> booksPage1 = bookRepository.getRankedBooks(page1);

        Pageable page2 = booksPage1.nextPageable();
        Page<BookEntity> booksPage2 = bookRepository.getRankedBooks(page2);
    }

    @ServiceConnection
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.5-bullseye");
}
