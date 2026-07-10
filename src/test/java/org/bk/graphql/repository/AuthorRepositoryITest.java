package org.bk.graphql.repository;

import org.bk.graphql.TimeTestData;
import org.bk.graphql.entity.AuthorEntity;
import org.bk.graphql.repository.author.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bk.graphql.TimeTestData.DEFAULT_CREATED_DATE;
import static org.bk.graphql.TimeTestData.DEFAULT_UPDATED_DATE;

@DataJdbcTest(properties = "spring.test.database.replace=NONE")
@Testcontainers
class AuthorRepositoryITest {

    @Autowired
    private AuthorRepository authorRepository;

    private Clock clock = TimeTestData.FIXED_CLOCK;

    @Test
    void test_authorRepositoryCrudOperations_withAuthor_returnsSavedUpdatedAndPagedAuthor() {
        AuthorEntity author = new AuthorEntity("id", "first last", DEFAULT_CREATED_DATE, DEFAULT_UPDATED_DATE, 0);
        authorRepository.save(author);
        assertThat(authorRepository.findById("id"))
                .hasValue(new AuthorEntity("id", "first last", DEFAULT_CREATED_DATE, DEFAULT_UPDATED_DATE, 1));
        assertThat(authorRepository.save(new AuthorEntity("id", "firstUpdated last", DEFAULT_CREATED_DATE, DEFAULT_UPDATED_DATE, 1)))
                .isEqualTo(new AuthorEntity("id", "firstUpdated last", author.createdAt(), DEFAULT_UPDATED_DATE, 2));

        assertThat(authorRepository.findById("id"))
                .hasValue(new AuthorEntity("id", "firstUpdated last", DEFAULT_CREATED_DATE, DEFAULT_UPDATED_DATE, 2));
        Page<AuthorEntity> page = authorRepository.findAll(Pageable.ofSize(5));
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @ServiceConnection
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine");
}
