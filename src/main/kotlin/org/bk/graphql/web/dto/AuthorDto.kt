package org.bk.graphql.web.dto

import org.bk.graphql.model.Author

data class AuthorDto(
    val id: String,
    val name: String,
    val version: Int

) {
    companion object {
        fun map(author: Author): AuthorDto {
            return AuthorDto(author.id, author.name, author.version)
        }
    }
}


