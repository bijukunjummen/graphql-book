package org.bk.graphql.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table("authors")
data class Author(
    @Id val id: String,
    val name: String,
    @Version val version: Int = 0
)
