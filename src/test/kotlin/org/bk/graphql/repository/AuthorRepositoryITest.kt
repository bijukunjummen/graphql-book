package org.bk.graphql.repository

import org.assertj.core.api.Assertions.assertThat
import org.bk.graphql.entity.AuthorEntity
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.data.domain.Pageable
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
        val author = AuthorEntity("id", "first last")
        authorRepository.save(author)
        assertThat(authorRepository.findById("id"))
            .hasValue(AuthorEntity("id", "first last", 1))
        assertThat(authorRepository.save(author.copy(name = "firstUpdated last", version = 1)))
            .isEqualTo(AuthorEntity("id", "firstUpdated last", 2))

        assertThat(authorRepository.findById("id"))
            .hasValue(AuthorEntity("id", "firstUpdated last", 2))
        val page = authorRepository.findAll(Pageable.ofSize(5))
        assertThat(page.totalElements).isEqualTo(1)
    }


    companion object {
        @ServiceConnection
        @Container
        @JvmStatic
        private val postgresContainer = PostgreSQLContainer("postgres:15.5-bullseye")
    }
}