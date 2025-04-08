package org.bk.graphql.web.dto

import org.bk.graphql.db.Book

data class BookDto(
    val id: String,
    val name: String,
    val pageCount: Int,
    val authorId: String,
    val version: Int = 0) {
    companion object {
        fun map(book: Book): BookDto {
            return BookDto(book.id, book.name, book.pageCount, book.authorId, book.version)
        }
    }

}
