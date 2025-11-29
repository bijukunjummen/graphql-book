package org.bk.graphql.web;

import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.Book;
import org.bk.graphql.domain.BookId;
import org.bk.graphql.service.AuthorService;
import org.bk.graphql.service.BookService;
import org.bk.graphql.service.CreateOrUpdateAuthorCommand;
import org.bk.graphql.service.CreateOrUpdateBookCommand;
import org.bk.graphql.web.dto.CreateAuthorInput;
import org.bk.graphql.web.dto.CreateBookInput;
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

import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BookControllerTest {

    private WebGraphQlTester graphQlTester;

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    private WebTestClient webTestClient;

    @Value("http://localhost:${local.server.port}")
    private String baseUrl;

    @BeforeEach
    void beforeEach() {
        webTestClient = WebTestClient.bindToServer()
            .baseUrl(baseUrl + "/graphql")
            .build();
        graphQlTester = HttpGraphQlTester.create(webTestClient);
    }

    @Test
    void shouldFindBookById() {
        var a12 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "c6aa1cb3-c9bd-47e0-ba1f-12a35027df8d",
                "Terry Pratchett"
            )
        );

        var a13 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "38469694-b350-4f1a-89be-1c8fd9aeaf2d",
                "Neil Gaiman"
            )
        );

        bookService.createOrUpdateBook(
            new CreateOrUpdateBookCommand(
                "2f5ac49b-af88-4e72-a549-5f86aff4e549",
                "Good Omens",
                490,
                Set.of(a12.id(), a13.id())
            )
        );

        String query = """
            query {
                findBookById(id: "2f5ac49b-af88-4e72-a549-5f86aff4e549") {
                    id
                    name
                    pageCount
                    version
                }
            }
            """;

        graphQlTester.document(query)
            .execute()
            .path("findBookById.id").entity(String.class).isEqualTo("2f5ac49b-af88-4e72-a549-5f86aff4e549")
            .path("findBookById.name").entity(String.class).isEqualTo("Good Omens")
            .path("findBookById.pageCount").entity(Integer.class).isEqualTo(490)
            .path("findBookById.version").entity(Integer.class).isEqualTo(1);
    }

    @Test
    void shouldCreateANewBook() {
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

        var authorMutationResponse = graphQlTester.document(authorCreationMutation)
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

        graphQlTester.document(mutation)
            .execute()
            .path("createBook.createdBook.name").entity(String.class).isEqualTo("New GraphQL Book")
            .path("createBook.createdBook.pageCount").entity(Integer.class).isEqualTo(250);
    }

    @ServiceConnection
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.5-bullseye");
}

