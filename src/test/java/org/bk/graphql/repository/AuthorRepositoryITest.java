package org.bk.graphql.repository;

import org.bk.graphql.entity.AuthorEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest(properties = "spring.test.database.replace=NONE")
@Testcontainers
class AuthorRepositoryITest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void testCrudOperations() {
        AuthorEntity author = new AuthorEntity("id", "first last", 0);
        authorRepository.save(author);
        assertThat(authorRepository.findById("id"))
            .hasValue(new AuthorEntity("id", "first last", 1));
        assertThat(authorRepository.save(new AuthorEntity("id", "firstUpdated last", 1)))
            .isEqualTo(new AuthorEntity("id", "firstUpdated last", 2));

        assertThat(authorRepository.findById("id"))
            .hasValue(new AuthorEntity("id", "firstUpdated last", 2));
        var page = authorRepository.findAll(Pageable.ofSize(5));
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @ServiceConnection
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.5-bullseye");
}

