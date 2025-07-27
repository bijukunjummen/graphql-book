package org.bk.graphql.web

import org.bk.graphql.domain.AuthorId
import org.bk.graphql.domain.Book
import org.bk.graphql.domain.BookId
import org.bk.graphql.service.AuthorService
import org.bk.graphql.service.BookService
import org.bk.graphql.service.CreateOrUpdateAuthorCommand
import org.bk.graphql.service.CreateOrUpdateBookCommand
import org.bk.graphql.web.dto.CreateAuthorInput
import org.bk.graphql.web.dto.CreateBookInput
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.graphql.test.tester.HttpGraphQlTester
import org.springframework.graphql.test.tester.WebGraphQlTester
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BookControllerTest {

    private lateinit var graphQlTester: WebGraphQlTester

    @Autowired
    private lateinit var bookService: BookService

    @Autowired
    private lateinit var authorService: AuthorService

    private lateinit var webTestClient: WebTestClient

    @Value("http://localhost:\${local.server.port}")
    private val baseUrl: String? = null

    @BeforeEach
    fun beforeEach() {
        webTestClient =
            WebTestClient.bindToServer()
                .baseUrl(baseUrl + "/graphql")
                .build()
        graphQlTester = HttpGraphQlTester.create(webTestClient)
    }
    @Test
    fun `should find book by id`() {
        val a12 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "c6aa1cb3-c9bd-47e0-ba1f-12a35027df8d",
                name = "Terry Pratchett"
            )
        )

        val a13 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "38469694-b350-4f1a-89be-1c8fd9aeaf2d",
                name = "Neil Gaiman"
            )
        )

        bookService.createOrUpdateBook(
            CreateOrUpdateBookCommand(
                id = "2f5ac49b-af88-4e72-a549-5f86aff4e549",
                name = "Good Omens",
                pageCount = 490,
                authors = setOf(a12.id, a13.id)
            )
        )

        val query = """
            query {
                findBookById(id: "2f5ac49b-af88-4e72-a549-5f86aff4e549") {
                    id
                    name
                    pageCount
                    version
                }
            }
        """.trimIndent()

        graphQlTester.document(query)
            .execute()
            .path("findBookById.id").entity(String::class.java).isEqualTo("2f5ac49b-af88-4e72-a549-5f86aff4e549")
            .path("findBookById.name").entity(String::class.java).isEqualTo("Good Omens")
            .path("findBookById.pageCount").entity(Int::class.java).isEqualTo(490)
            .path("findBookById.version").entity(Int::class.java).isEqualTo(1)
    }

    @Test
    fun `should create a new book`() {

        val authorCreationMutation = """
            mutation myMutation(${'$'}createAuthorInput: CreateAuthorInput) {
              createAuthor(input: ${'$'}createAuthorInput) {
                author{
                id
                name
                version
                }
              }
            }
        """.trimIndent()

        val authorMutationResponse = graphQlTester.document(authorCreationMutation)
            .variable("createAuthorInput", CreateAuthorInput(name = "test author"))
            .execute()

        val authorId: String = authorMutationResponse
            .path("createAuthor.author.id")
            .entity(String::class.java)
            .get()

        val book = Book(
            id = BookId("new-book-1"),
            name = "New GraphQL Book",
            pageCount = 250,
            authors = listOf(AuthorId(authorId)),
            version = 1
        )

        val mutation = """
            mutation {
                createBook(input: {
                    name: "New GraphQL Book",
                    pageCount: 250,
                    authors: ["$authorId"]
                }) {
                    book {
                        id
                        name
                        pageCount
                        version
                    }
                }
            }
        """.trimIndent()

        graphQlTester.document(mutation)
            .execute()
            .path("createBook.book.name").entity(String::class.java).isEqualTo("New GraphQL Book")
            .path("createBook.book.pageCount").entity(Int::class.java).isEqualTo(250)
    }

    companion object {
        @ServiceConnection
        @Container
        @JvmStatic
        private val postgresContainer = PostgreSQLContainer("postgres:15.5-bullseye")
    }
}
