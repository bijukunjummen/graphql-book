package org.bk.graphql.domain

data class Book(
    val id: BookId,
    val name: String,
    val pageCount: Int,
    val authors: List<AuthorId>,
    val version: Int
)
