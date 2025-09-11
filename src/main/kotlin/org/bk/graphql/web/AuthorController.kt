package org.bk.graphql.web

import org.bk.graphql.domain.Author
import org.bk.graphql.domain.AuthorId
import org.bk.graphql.domain.Book
import org.bk.graphql.domain.BookId
import org.bk.graphql.entity.BookEntity
import org.bk.graphql.service.AuthorService
import org.bk.graphql.service.BookService
import org.bk.graphql.service.ById
import org.bk.graphql.service.ByIds
import org.bk.graphql.service.CreateAuthorCommand
import org.bk.graphql.web.dto.AuthorDto
import org.bk.graphql.web.dto.BookDto
import org.bk.graphql.web.dto.CreateAuthorInput
import org.bk.graphql.web.dto.CreateAuthorPayload
import org.dataloader.DataLoader
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.BatchMapping
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.graphql.execution.BatchLoaderRegistry
import org.springframework.stereotype.Controller
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors

@Controller
class AuthorController(
    private val authorService: AuthorService,
    private val bookService: BookService
) {

    @QueryMapping
    fun findAuthorById(@Argument id: String): AuthorDto {
        return authorService.getAuthor(ById(AuthorId(id))).let { AuthorDto.map(it) }
    }

    @MutationMapping
    fun createAuthor(@Argument input: CreateAuthorInput): CreateAuthorPayload {
        val createdAuthor = authorService.createAuthor(CreateAuthorCommand(input.name))
        return CreateAuthorPayload(AuthorDto.map(createdAuthor))
    }

    @SchemaMapping(typeName = "Book")
    fun authors(bookDto: BookDto, dataLoader: DataLoader<BookId, AuthorsWrapper>): CompletableFuture<List<AuthorDto>> {
        return dataLoader.load(BookId(bookDto.id)).thenApply { wrapper -> wrapper.authors }
    }

//    @BatchMapping(typeName = "Book")
//    fun authors(books: Set<BookDto>): Map<BookDto, List<AuthorDto>> {
//        val bookIds: List<BookId> = books.map { book -> BookId(book.id) }
//        val authorsForBooks: Map<BookId, List<Author>> = bookService.getAuthorsForBooks(ByIds(bookIds))
//        val result: Map<BookDto, List<AuthorDto>> = books.map {book ->
//            val authors = authorsForBooks[BookId(book.id)]!!.map { author -> AuthorDto.map(author) }
//            book to authors
//        }.toMap()
//        return result
//    }
}

