package org.bk.graphql.web.dto

import org.bk.graphql.domain.Author

data class AuthorDto(
    val id: String,
    val name: String,
    val version: Int

) {
    companion object {
        fun map(author: Author): AuthorDto {
            return AuthorDto(author.id.id, author.name, author.version)
        }
    }
}


