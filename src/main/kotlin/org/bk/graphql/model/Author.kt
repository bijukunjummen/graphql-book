package org.bk.graphql.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table("authors")
data class Author(
    @Id val id: String,
    val firstName: String,
    val lastName: String,
    @Version val version: Int = 0
)
