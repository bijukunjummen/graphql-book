package org.bk.graphql.service

import org.bk.graphql.domain.Author
import org.bk.graphql.domain.Book
import org.bk.graphql.domain.BookId
import org.springframework.data.domain.Page
import java.util.Optional

interface BookService {
    fun createBook(createBookCommand: CreateBookCommand): Book
    fun createOrUpdateBook(createOrUpdateBookCommand: CreateOrUpdateBookCommand): Book
    fun updateBook(updateBookCommand: UpdateBookCommand): Book
    fun getBooks(getBooksQuery: GetBooksQuery): Page<Book>
    fun getBook(byIdQuery: ById<BookId>): Optional<Book>
    fun getBooks(byIdQuery: ByIds<BookId>): List<Book>
    fun getAuthorsForBooks(ids: ByIds<BookId>): Map<BookId, Set<Author>>
}