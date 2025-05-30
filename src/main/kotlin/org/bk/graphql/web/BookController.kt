package org.bk.graphql.web

import org.bk.graphql.model.Book
import org.bk.graphql.repository.BookRepository
import org.bk.graphql.service.BookService
import org.bk.graphql.service.ById
import org.bk.graphql.service.CreateBookCommand
import org.bk.graphql.web.dto.BookDto
import org.bk.graphql.web.dto.CreateBookInput
import org.bk.graphql.web.dto.CreateBookPayload
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class BookController(private val bookService: BookService) {

    @QueryMapping
    fun findBookById(@Argument id: String): BookDto {
        return BookDto.map(bookService.getBook(ById(id)).orElseThrow())
    }

    @MutationMapping
    fun createBook(book: CreateBookInput): CreateBookPayload {
        val createdBook: Book = bookService.createBook(
            CreateBookCommand(
                name = book.name,
                pageCount = book.pageCount,
                authors = book.authors
            )
        )
        return CreateBookPayload(BookDto.map(createdBook))
    }
}