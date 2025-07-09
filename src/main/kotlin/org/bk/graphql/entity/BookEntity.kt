package org.bk.graphql.entity

import org.bk.graphql.domain.AuthorId
import org.bk.graphql.domain.Book
import org.bk.graphql.domain.BookId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table

@Table("books")
data class BookEntity(
    @Id val id: String,
    val name: String,
    val pageCount: Int,
    @MappedCollection(idColumn = "book_id")
    val authors: Set<AuthorRef>,
    @Version val version: Int = 0
) {
    fun toModel() = Book(
        id = BookId(id),
        name = name,
        pageCount = pageCount,
        authors = authors.stream().map { author -> AuthorId(author.author.id!!) }.toList(),
        version = version
        )
}

