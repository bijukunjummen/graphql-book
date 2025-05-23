package org.bk.graphql.model

import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("book_author")
data class AuthorRef (
    @Column("author_id") val author: AggregateReference<Author, String>
)