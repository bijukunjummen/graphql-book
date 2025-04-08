package org.bk.graphql

import org.bk.graphql.db.Author
import org.bk.graphql.db.Book
import org.bk.graphql.repository.AuthorRepository
import org.bk.graphql.repository.BookRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.graphql.test.tester.WebGraphQlTester
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureHttpGraphQlTester
class BookControllerTests {
    @Autowired
    lateinit var graphQlTester: WebGraphQlTester

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var authorRepository: AuthorRepository


    @Test
    fun testPaginate() {
        bookRepository.deleteAll()
        authorRepository.deleteAll()

        authorRepository.save(Author(id = "author-id-1", firstName = "first", lastName = "last"))
        bookRepository.save(Book(id = "book-id-1", name = "Test Book - 1", pageCount = 100, authorId = "author-id-1"))
        bookRepository.save(Book(id = "book-id-2", name = "Test Book - 2", pageCount = 100, authorId = "author-id-1"))
        val document1 = """
            query MyQuery {
              allBooks(first: 1) {
                pageInfo {
                  startCursor
                  endCursor
                  hasNextPage
                  hasPreviousPage
                }
                edges {
                  cursor
                  node {
                    id
                    name
                    pageCount
                    author {
                      id
                      firstName
                      lastName
                      version
                    }
                  }
                }
              }
            }
        """.trimIndent()

        val nodePath: GraphQlTester.Response = graphQlTester.document(document1)
            .execute()

        nodePath.path("allBooks.edges[*].node.name")
            .entityList(String::class.java)
            .containsExactly("Test Book - 1")

        val endCursor = nodePath.path("allBooks.pageInfo.endCursor").entity(String::class.java).get()
        val document2 = """
            query MyQuery {
              allBooks(first: 1, after: "$endCursor") {
                pageInfo {
                  startCursor
                  endCursor
                  hasNextPage
                  hasPreviousPage
                }
                edges {
                  cursor
                  node {
                    id
                    name
                    pageCount
                    author {
                      id
                      firstName
                      lastName
                      version
                    }
                  }
                }
              }
            }
        """.trimIndent()

        graphQlTester.document(document2)
            .execute()
            .path("allBooks.edges[*].node.name")
            .entityList(String::class.java)
            .containsExactly("Test Book - 2")
    }

    companion object {
        @ServiceConnection
        @Container
        @JvmStatic
        private val postgresContainer = PostgreSQLContainer("postgres:15.5-bullseye")
    }
}