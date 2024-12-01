package org.bk.graphql.service

import org.bk.graphql.model.Book
import org.springframework.data.domain.Page

interface BookService {
    fun createBook(createBookCommand: CreateBookCommand): Book
    fun createOrUpdateBook(createOrUpdateBookCommand: CreateOrUpdateBookCommand): Book
    fun updateBook(updateBookCommand: UpdateBookCommand): Book
    fun getBooks(getBooksQuery: GetBooksQuery): Page<Book>
}