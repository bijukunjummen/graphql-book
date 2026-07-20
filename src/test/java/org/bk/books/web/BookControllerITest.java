package org.bk.books.web;

import io.floci.testcontainers.FlociContainer;
import java.util.List;
import org.bk.books.application.BookAuthorManagementService;
import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.Book;
import org.bk.books.domain.entity.book.BookId;
import org.bk.books.service.author.AuthorService;
import org.bk.books.service.author.AuthorServiceCommands.CreateOrUpdateAuthorCommand;
import org.bk.books.service.book.BookCommands.CreateOrUpdateBookCommand;
import org.bk.books.web.dto.CreateAuthorInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BookControllerITest {

    private WebGraphQlTester graphQlTester;

    @Autowired
    private BookAuthorManagementService bookAuthorManagementService;

    @Autowired
    private AuthorService authorService;

    private WebTestClient webTestClient;

    @Value("http://localhost:${local.server.port}")
    private String baseUrl;

    @BeforeEach
    void beforeEach() {
        webTestClient =
                WebTestClient.bindToServer().baseUrl(baseUrl + "/graphql").build();
        graphQlTester = HttpGraphQlTester.create(webTestClient);
    }

    @Test
    void test_findBookById_withExistingBook_returnsBook() {
        Author a12 = authorService.createOrUpdateAuthor(new CreateOrUpdateAuthorCommand(
                AuthorId.parse("c6aa1cb3-c9bd-47e0-ba1f-12a35027df8d"), "Terry Pratchett"));

        Author a13 = authorService.createOrUpdateAuthor(
                new CreateOrUpdateAuthorCommand(AuthorId.parse("38469694-b350-4f1a-89be-1c8fd9aeaf2d"), "Neil Gaiman"));

        bookAuthorManagementService.createOrUpdateBook(new CreateOrUpdateBookCommand(
                BookId.parse("2f5ac49b-af88-4e72-a549-5f86aff4e549"), "Good Omens", 490, List.of(a12.id(), a13.id())));

        String query = """
                query {
                    findBookById(id: "2f5ac49b-af88-4e72-a549-5f86aff4e549") {
                        id
                        name
                        pageCount
                        version
                        authors {
                          id
                          name
                        }
                    }
                }
                """;

        graphQlTester
                .document(query)
                .execute()
                .path("findBookById.id")
                .entity(String.class)
                .isEqualTo("2f5ac49b-af88-4e72-a549-5f86aff4e549")
                .path("findBookById.name")
                .entity(String.class)
                .isEqualTo("Good Omens")
                .path("findBookById.authors[0].name")
                .entity(String.class)
                .isEqualTo("Terry Pratchett")
                .path("findBookById.pageCount")
                .entity(Integer.class)
                .isEqualTo(490)
                .path("findBookById.version")
                .entity(Integer.class)
                .isEqualTo(1);
    }

    @Test
    void test_createBook_withValidInput_returnsCreatedBook() {
        String authorCreationMutation = """
                mutation myMutation($createAuthorInput: CreateAuthorInput) {
                  createAuthor(input: $createAuthorInput) {
                    createdAuthor{
                    id
                    name
                    version
                    }
                  }
                }
                """;

        var authorMutationResponse = graphQlTester
                .document(authorCreationMutation)
                .variable("createAuthorInput", new CreateAuthorInput("test author"))
                .execute();

        String authorId = authorMutationResponse
                .path("createAuthor.createdAuthor.id")
                .entity(String.class)
                .get();

        String mutation = """
                mutation {
                    createBook(input: {
                        name: "New GraphQL Book",
                        pageCount: 250,
                        authors: ["%s"]
                    }) {
                        createdBook {
                            id
                            name
                            pageCount
                            version
                        }
                    }
                }
                """.formatted(authorId);

        graphQlTester
                .document(mutation)
                .execute()
                .path("createBook.createdBook.name")
                .entity(String.class)
                .isEqualTo("New GraphQL Book")
                .path("createBook.createdBook.pageCount")
                .entity(Integer.class)
                .isEqualTo(250);
    }

    @Test
    void test_updateBookName_withExistingBook_returnsRenamedBook() {
        var author = authorService.createOrUpdateAuthor(new CreateOrUpdateAuthorCommand(
                AuthorId.parse("f3b5bb7e-1f73-4ef0-bdc3-ef4f5e1d8c1e"), "Ursula K. Le Guin"));

        Book book = bookAuthorManagementService.createOrUpdateBook(new CreateOrUpdateBookCommand(
                BookId.parse("d8d387ac-0b36-4d33-b6d2-9f1a4591ec35"),
                "A Wizard of Earthsea",
                205,
                List.of(author.id())));

        String mutation = """
                mutation {
                    updateBookName(input: {
                        id: "d8d387ac-0b36-4d33-b6d2-9f1a4591ec35",
                        name: "The Tombs of Atuan",
                        version: %d
                    }) {
                        book {
                            id
                            name
                            pageCount
                            version
                        }
                    }
                }
                """.formatted(book.version());

        graphQlTester
                .document(mutation)
                .execute()
                .path("updateBookName.book.id")
                .entity(String.class)
                .isEqualTo("d8d387ac-0b36-4d33-b6d2-9f1a4591ec35")
                .path("updateBookName.book.name")
                .entity(String.class)
                .isEqualTo("The Tombs of Atuan")
                .path("updateBookName.book.pageCount")
                .entity(Integer.class)
                .isEqualTo(205)
                .path("updateBookName.book.version")
                .entity(Integer.class)
                .isEqualTo(book.version() + 1);
    }

    @ServiceConnection
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.5-bullseye");

    @Container
    @ServiceConnection
    private static final FlociContainer flociContainer = new FlociContainer();
}
