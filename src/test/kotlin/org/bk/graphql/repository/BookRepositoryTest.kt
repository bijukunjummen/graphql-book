package org.bk.graphql.repository

import org.bk.graphql.model.Author
import org.bk.graphql.model.Book
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@DataJdbcTest(properties = ["spring.test.database.replace=NONE"])
@Testcontainers
class BookRepositoryTest {

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Test
    fun testCrudOperations() {
        val author = Author(id = "author-id", firstName = "first", lastName = "last")
        authorRepository.save(author)

        val book = Book(id = "id", name = "name", 100, "author-id")
        bookRepository.save(book)
    }


    companion object {
        @ServiceConnection
        @Container
        @JvmStatic
        private val postgresContainer = PostgreSQLContainer("postgres:15.5-bullseye")
    }
}