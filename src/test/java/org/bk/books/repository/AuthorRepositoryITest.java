package org.bk.books.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import org.bk.books.AuthorTestData;
import org.bk.books.TimeTestData;
import org.bk.books.entity.AuthorEntity;
import org.bk.books.repository.author.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJdbcTest(properties = "spring.test.database.replace=NONE")
@Testcontainers
class AuthorRepositoryITest {

  @Autowired private AuthorRepository authorRepository;

  private Clock clock = TimeTestData.FIXED_CLOCK;

  @Test
  void test_authorRepositoryCrudOperations_withAuthor_returnsSavedUpdatedAndPagedAuthor() {
    AuthorEntity author = AuthorEntity.fromModel(AuthorTestData.sampleAuthor_1());
    authorRepository.save(author);
    assertThat(authorRepository.findById(author.id()))
        .hasValue(
            new AuthorEntity(
                author.id(), author.name(), author.createdAt(), author.updatedAt(), 1));
    AuthorEntity updatedAuthor =
        new AuthorEntity(
            author.id(), "firstUpdated last", author.createdAt(), author.updatedAt(), 1);
    authorRepository.save(updatedAuthor);
    assertThat(authorRepository.findById(author.id()))
        .hasValue(
            new AuthorEntity(
                author.id(),
                "firstUpdated last",
                updatedAuthor.createdAt(),
                updatedAuthor.updatedAt(),
                2));
    Page<AuthorEntity> page = authorRepository.findAll(Pageable.ofSize(5));
    assertThat(page.getTotalElements()).isEqualTo(1);
  }

  @ServiceConnection @Container
  private static final PostgreSQLContainer<?> postgresContainer =
      new PostgreSQLContainer<>("postgres:16-alpine");
}
