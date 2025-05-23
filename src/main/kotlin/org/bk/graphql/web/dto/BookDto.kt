package org.bk.graphql.web.dto

import org.bk.graphql.model.Book

data class BookDto(
    val id: String,
    val name: String,
    val pageCount: Int,
    val version: Int = 0
) {
    companion object {
        fun map(book: Book): BookDto = BookDto(book.id, book.name, book.version)
    }
}