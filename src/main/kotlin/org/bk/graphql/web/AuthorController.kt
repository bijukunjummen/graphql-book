package org.bk.graphql.web

import org.bk.graphql.model.Book
import org.bk.graphql.service.AuthorService
import org.bk.graphql.service.CreateAuthorCommand
import org.bk.graphql.service.GetAuthorQuery
import org.bk.graphql.web.model.AuthorDto
import org.bk.graphql.web.model.CreateAuthorInput
import org.bk.graphql.web.model.CreateAuthorPayload
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class AuthorController(private val authorService: AuthorService) {

    @QueryMapping
    fun findAuthorById(@Argument id: String): AuthorDto {
        return authorService.getAuthor(GetAuthorQuery(id)).let { AuthorDto.map(it) }
    }

    @MutationMapping
    fun createAuthor(@Argument input: CreateAuthorInput): CreateAuthorPayload {
        val createdAuthor = authorService.createAuthor(CreateAuthorCommand(input.firstName, input.lastName))
        return CreateAuthorPayload(AuthorDto.map(createdAuthor))
    }

    @SchemaMapping(typeName = "Book", field = "author")
    fun author(book: Book): AuthorDto {
        val author = authorService.getAuthor(GetAuthorQuery(book.authorId))
        return AuthorDto.map(author)
    }
}