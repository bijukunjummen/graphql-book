package org.bk.graphql.service

import org.bk.graphql.domain.Book
import org.springframework.data.domain.Page
import java.util.Optional

interface BookService {
    fun createBook(createBookCommand: CreateBookCommand): Book
    fun createOrUpdateBook(createOrUpdateBookCommand: CreateOrUpdateBookCommand): Book
    fun updateBook(updateBookCommand: UpdateBookCommand): Book
    fun getBooks(getBooksQuery: GetBooksQuery): Page<Book>
    fun getBook(byIdQuery: ById): Optional<Book>
    fun getBooks(byIdQuery: ByIds): List<Book>
}