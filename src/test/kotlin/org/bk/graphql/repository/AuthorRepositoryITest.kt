package org.bk.graphql.repository

import org.assertj.core.api.Assertions.assertThat
import org.bk.graphql.model.Author
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@DataJdbcTest(properties = ["spring.test.database.replace=NONE"])
@Testcontainers
class AuthorRepositoryITest {

    @Autowired
    private lateinit var authorRepository: AuthorRepository


    @Test
    fun testCrudOperations() {
        val author = Author("id", "first", "last")
        authorRepository.save(author)
        assertThat(authorRepository.findById("id"))
            .hasValue(Author("id", "first", "last", 1))
    }


    companion object {
        @ServiceConnection
        @Container
        @JvmStatic
        private val postgresContainer = PostgreSQLContainer("postgres:15.5-bullseye")
    }
}