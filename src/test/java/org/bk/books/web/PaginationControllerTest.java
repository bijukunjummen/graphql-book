package org.bk.books.web;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PaginationControllerTest {
    private WebGraphQlTester graphQlTester;

    @Value("http://localhost:${local.server.port}")
    private String baseUrl;

    @BeforeEach
    void beforeEach() {
        WebTestClient webTestClient = WebTestClient.bindToServer()
                .baseUrl(baseUrl + "/graphql")
                .responseTimeout(Duration.ofSeconds(60))
                .build();
        graphQlTester = HttpGraphQlTester.create(webTestClient);
        graphQlTester
                .document("""
            mutation {
              loadSampleData {
                authorsLoaded
                booksLoaded
              }
            }
            """)
                .execute()
                .path("loadSampleData.authorsLoaded")
                .entity(Integer.class)
                .isEqualTo(13)
                .path("loadSampleData.booksLoaded")
                .entity(Integer.class)
                .isEqualTo(12);
    }

    @Test
    void test_findAuthors_whenPageSizeChangesAfterEndCursor_returnsNextAuthorPage() {
        String query = """
            query authorPage($first: Int, $after: String) {
              findAuthors(first: $first, after: $after, sort: [{ field: "name", order: ASC }]) {
                edges {
                  cursor
                  node {
                    name
                  }
                }
                pageInfo {
                  hasNextPage
                  hasPreviousPage
                  endCursor
                }
                totalCount
              }
            }
            """;

        GraphQlTester.Response firstPage =
                graphQlTester.document(query).variable("first", 2).execute();

        String endCursor = firstPage
                .path("findAuthors.pageInfo.endCursor")
                .entity(String.class)
                .get();

        firstPage
                .path("findAuthors.edges[*].node.name")
                .entityList(String.class)
                .containsExactly("Aldous Huxley", "Dan Simmons");
        firstPage.path("findAuthors.pageInfo.hasNextPage").entity(Boolean.class).isEqualTo(true);
        firstPage
                .path("findAuthors.pageInfo.hasPreviousPage")
                .entity(Boolean.class)
                .isEqualTo(false);

        GraphQlTester.Response secondPage = graphQlTester
                .document(query)
                .variable("first", 3)
                .variable("after", endCursor)
                .execute();

        secondPage
                .path("findAuthors.edges[*].node.name")
                .entityList(String.class)
                .containsExactly("Douglas Adams", "Frank Herbert", "George Orwell");
        secondPage
                .path("findAuthors.pageInfo.hasNextPage")
                .entity(Boolean.class)
                .isEqualTo(true);
        secondPage
                .path("findAuthors.pageInfo.hasPreviousPage")
                .entity(Boolean.class)
                .isEqualTo(true);
        secondPage.path("findAuthors.totalCount").entity(Integer.class).isEqualTo(13);
    }

    @Test
    void test_findBooks_whenPageSizeChangesAfterEndCursor_returnsNextBookPage() {
        String query = """
            query bookPage($first: Int, $after: String) {
              findBooks(first: $first, after: $after, sort: [{ field: "name", order: ASC }]) {
                edges {
                  cursor
                  node {
                    name
                  }
                }
                pageInfo {
                  hasNextPage
                  hasPreviousPage
                  endCursor
                }
                totalCount
              }
            }
            """;

        GraphQlTester.Response firstPage =
                graphQlTester.document(query).variable("first", 3).execute();

        String endCursor = firstPage
                .path("findBooks.pageInfo.endCursor")
                .entity(String.class)
                .get();

        firstPage
                .path("findBooks.edges[*].node.name")
                .entityList(String.class)
                .containsExactly("1984", "Brave New World", "Do Androids Dream of Electric Sheep");
        firstPage.path("findBooks.pageInfo.hasNextPage").entity(Boolean.class).isEqualTo(true);
        firstPage
                .path("findBooks.pageInfo.hasPreviousPage")
                .entity(Boolean.class)
                .isEqualTo(false);

        GraphQlTester.Response secondPage = graphQlTester
                .document(query)
                .variable("first", 2)
                .variable("after", endCursor)
                .execute();

        secondPage
                .path("findBooks.edges[*].node.name")
                .entityList(String.class)
                .containsExactly("Dune", "Ender's Game");
        secondPage.path("findBooks.pageInfo.hasNextPage").entity(Boolean.class).isEqualTo(true);
        secondPage
                .path("findBooks.pageInfo.hasPreviousPage")
                .entity(Boolean.class)
                .isEqualTo(true);
        secondPage.path("findBooks.totalCount").entity(Integer.class).isEqualTo(12);
    }

    @ServiceConnection
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.5-bullseye");
}
