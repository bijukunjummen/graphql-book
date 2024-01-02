package org.bk.graphql.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table("books")
data class Book(
    @Id val id: String,
    val name: String,
    val pageCount: Int,
    val authorId: String,
    @Version val version: Int = 0)
