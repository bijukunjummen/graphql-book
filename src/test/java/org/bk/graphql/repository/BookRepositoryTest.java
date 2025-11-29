package org.bk.graphql.repository;

import org.bk.graphql.entity.AuthorEntity;
import org.bk.graphql.entity.AuthorRef;
import org.bk.graphql.entity.BookEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest(properties = "spring.test.database.replace=NONE")
@Testcontainers
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void testCrudOperations() {
        AuthorEntity author = new AuthorEntity("author-id", "first last", 0);
        authorRepository.save(author);

        BookEntity book = new BookEntity(
            "id",
            "name",
            100,
            Set.of(new AuthorRef(AggregateReference.to("author-id"))),
            0
        );
        bookRepository.save(book);
        assertThat(bookRepository.findById(book.id()).orElseThrow().name()).isEqualTo("name");
    }

    @ServiceConnection
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.5-bullseye");
}

