package org.bk.graphql.web

import org.bk.graphql.model.Book
import org.bk.graphql.repository.BookRepository
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class BookController(private val bookRepository: BookRepository) {

    @QueryMapping
    fun findBookById(@Argument id: String): Book {
        return bookRepository.findById(id).orElseThrow()
    }


}