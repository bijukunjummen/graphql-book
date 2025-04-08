package org.bk.graphql.db

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("authors")
data class Author(
    @Id val id: String,
    @Column("first_name")
    val firstName: String,
    @Column("last_name")
    val lastName: String,
    @Version val version: Int = 0
)
