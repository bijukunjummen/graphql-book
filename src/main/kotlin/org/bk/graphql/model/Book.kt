package org.bk.graphql.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table

@Table("books")
data class Book(
    @Id val id: String,
    val name: String,
    val pageCount: Int,
    @MappedCollection(idColumn = "book_id")
    val authors: Set<AuthorRef>,
    @Version val version: Int = 0)
