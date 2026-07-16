package org.bk.books.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bk.books.AuthorTestData.sampleAuthor_1;
import static org.bk.books.TimeTestData.DEFAULT_CREATED_DATE;
import static org.bk.books.TimeTestData.DEFAULT_UPDATED_DATE;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import org.assertj.core.api.SoftAssertions;
import org.bk.books.BookAuthorLinkTestData;
import org.bk.books.BookTestData;
import org.bk.books.entity.AuthorEntity;
import org.bk.books.entity.BookAuthorLinkEntity;
import org.bk.books.entity.BookEntity;
import org.bk.books.repository.author.AuthorEntityRepository;
import org.bk.books.repository.book.BookEntityEntityRepository;
import org.bk.books.repository.bookauthorlink.BookAuthorLinkEntityRepository;
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

@DataJdbcTest(properties = "spring.test.database.replace=NONE")
@Testcontainers
class BookEntityRepositoryITest {

    @Autowired
    private BookEntityEntityRepository bookEntityRepository;

    @Autowired
    private AuthorEntityRepository authorEntityRepository;

    @Autowired
    private BookAuthorLinkEntityRepository bookAuthorLinkEntityRepository;

    @Test
    void test_crudOperations_withBookAndAuthor_returnsSavedBook() {
        AuthorEntity author = AuthorEntity.fromModel(sampleAuthor_1());
        authorEntityRepository.save(author);

        BookEntity book = BookEntity.fromModel(BookTestData.sampleBook());
        bookEntityRepository.save(book);
        bookAuthorLinkEntityRepository.save(
                BookAuthorLinkEntity.fromModel(BookAuthorLinkTestData.sampleBookAuthorLink()));
        assertThat(bookEntityRepository.findById(book.id()).orElseThrow().name())
                .isEqualTo(book.name());
    }

    @Test
    void test_getBooks_returnsPaginatedResults() {
        AuthorEntity author1 = AuthorEntity.fromModel(sampleAuthor_1());
        AuthorEntity savedAuthor = authorEntityRepository.save(author1);

        List<BookEntity> bookEntityList = IntStream.range(0, 10)
                .mapToObj(i -> new BookEntity(
                        UUID.randomUUID(), "name-" + i, 100, DEFAULT_CREATED_DATE, DEFAULT_UPDATED_DATE, 0))
                .toList();
        List<BookEntity> savedBookEntities = StreamSupport.stream(
                        bookEntityRepository.saveAll(bookEntityList).spliterator(), false)
                .toList();

        bookEntityList.forEach(book -> {
            bookAuthorLinkEntityRepository.save(new BookAuthorLinkEntity(
                    UUID.randomUUID(), book.id(), savedAuthor.id(), DEFAULT_CREATED_DATE, DEFAULT_UPDATED_DATE));
        });

        Pageable page1 = PageRequest.of(0, 2);

        Page<BookEntity> booksPage1 = bookEntityRepository.getRankedBooks(page1);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(booksPage1.getTotalElements()).isEqualTo(10);
            softly.assertThat(booksPage1.getTotalPages()).isEqualTo(5);
            softly.assertThat(booksPage1.getContent()).isEqualTo(savedBookEntities.subList(0, 2));
        });

        Pageable page2 = booksPage1.nextPageable();
        Page<BookEntity> booksPage2 = bookEntityRepository.getRankedBooks(page2);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(booksPage2.getTotalElements()).isEqualTo(10);
            softly.assertThat(booksPage2.getTotalPages()).isEqualTo(5);
            softly.assertThat(booksPage2.getContent()).isEqualTo(savedBookEntities.subList(2, 4));
        });
    }

    @ServiceConnection
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.5-bullseye");
}
