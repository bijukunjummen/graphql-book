package org.bk.graphql.web

import org.bk.graphql.service.AuthorService
import org.bk.graphql.service.BookService
import org.bk.graphql.service.ById
import org.bk.graphql.service.ByIds
import org.bk.graphql.service.CreateAuthorCommand
import org.bk.graphql.web.dto.AuthorDto
import org.bk.graphql.web.dto.BookDto
import org.bk.graphql.web.dto.CreateAuthorInput
import org.bk.graphql.web.dto.CreateAuthorPayload
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class AuthorController(private val authorService: AuthorService, private val bookService: BookService) {

    @QueryMapping
    fun findAuthorById(@Argument id: String): AuthorDto {
        return authorService.getAuthor(ById(id)).let { AuthorDto.map(it) }
    }

    @MutationMapping
    fun createAuthor(@Argument input: CreateAuthorInput): CreateAuthorPayload {
        val createdAuthor = authorService.createAuthor(CreateAuthorCommand(input.name))
        return CreateAuthorPayload(AuthorDto.map(createdAuthor))
    }

    @SchemaMapping(typeName = "Book", field = "authors")
    fun author(book: BookDto): List<AuthorDto> {
        val rawBook = bookService.getBook(ById(book.id)).orElseThrow()
        val authorIds: List<String> = rawBook.authors.map { ref -> ref.author.id!! }
        return authorService.getAuthors(ByIds(authorIds)).map { AuthorDto.map(it) }
    }
}