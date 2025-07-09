package org.bk.graphql.web.dto

import org.bk.graphql.domain.Book

data class BookDto(
    val id: String,
    val name: String,
    val pageCount: Int,
    val version: Int = 0
) {
    companion object {
        fun map(book: Book): BookDto = BookDto(book.id.id, book.name, pageCount = book.pageCount, version = book.version)
    }
}