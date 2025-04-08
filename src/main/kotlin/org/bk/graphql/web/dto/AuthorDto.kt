package org.bk.graphql.web.dto

import org.bk.graphql.db.Author

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


