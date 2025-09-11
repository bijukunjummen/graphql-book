package org.bk.graphql.service

import org.bk.graphql.domain.Author
import org.bk.graphql.domain.Book
import org.bk.graphql.domain.BookId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional

interface BookService {
    fun createBook(command: CreateBookCommand): Book
    fun createOrUpdateBook(command: CreateOrUpdateBookCommand): Book
    fun updateBook(command: UpdateBookCommand): Book
    fun getBooks(query: GetBooksQuery): Page<Book>
    fun getBooks(pageable: Pageable): Page<Book>
    fun getBook(query: ById<BookId>): Optional<Book>
    fun getBooks(query: ByIds<BookId>): List<Book>
    fun getAuthorsForBooks(ids: ByIds<BookId>): Map<BookId, List<Author>>
}