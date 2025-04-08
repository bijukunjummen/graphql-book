package org.bk.graphql.db

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("books")
data class Book(
    @Id val id: String,
    @Column("name")
    val name: String,
    @Column("page_count")
    val pageCount: Int,
    @Column("author_id")
    val authorId: String,
    @Version val version: Int = 0)
