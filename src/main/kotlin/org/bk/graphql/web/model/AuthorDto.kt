package org.bk.graphql.web.model

import org.bk.graphql.model.Author

data class AuthorDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val version: Int

) {
    companion object {
        fun map(author: Author): AuthorDto {
            return AuthorDto(author.id, author.firstName, author.lastName, author.version)
        }
    }
}


