package org.bk.graphql.entity

import org.bk.graphql.domain.Author
import org.bk.graphql.domain.AuthorId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table("authors")
data class AuthorEntity(
    @Id val id: String,
    val name: String,
    @Version val version: Int = 0
) {
    fun toModel(): Author = Author(AuthorId(id), name, version)
}
