package org.bk.graphql.web

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
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.BatchMapping
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.stream.Collectors

@Controller
class AuthorController(private val authorService: AuthorService, private val bookService: BookService) {

    @QueryMapping
    fun findAuthorById(@Argument id: String): AuthorDto {
        return authorService.getAuthor(ById(AuthorId(id))).let { AuthorDto.map(it) }
    }

    @MutationMapping
    fun createAuthor(@Argument input: CreateAuthorInput): CreateAuthorPayload {
        val createdAuthor = authorService.createAuthor(CreateAuthorCommand(input.name))
        return CreateAuthorPayload(AuthorDto.map(createdAuthor))
    }

//    @SchemaMapping(typeName = "Book", field = "authors")
//    fun authors(book: BookDto): List<AuthorDto> {
//        val rawBook = bookService.getBook(ById(book.id)).orElseThrow()
//        val authorIds: List<String> = rawBook.authors.map { ref -> ref.author.id!! }
//        return authorService.getAuthors(ByIds(authorIds)).map { AuthorDto.map(it) }
//    }

    @BatchMapping(typeName = "Book")
    fun authors(books: Set<BookDto>): Map<BookDto, List<AuthorDto>> {
        val bookDtoById: Map<BookId, BookDto> = books.stream().collect(Collectors.toMap({ b -> BookId(b.id) }, { it }))
        val booksFromDb: List<Book> = bookService.getBooks(ByIds<BookId>(books.map { BookId(it.id) }))
        val authorIds: List<String> = booksFromDb.stream().flatMap { book -> book.authors.stream().map { ref -> ref.id } }.toList()
        val authorByIds: Map<AuthorId, AuthorDto> =
            authorService.getAuthors(ByIds(authorIds.map { AuthorId(it) }))
                .stream().collect(Collectors.toMap({ a -> a.id }, { a -> AuthorDto.map(a) }))

        val result: Map<BookDto, List<AuthorDto>> = booksFromDb.map {book ->
            val bookDto = bookDtoById[book.id]!!
            val authors = book.authors.map { authorId -> authorByIds[authorId]!! }.toList()
            bookDto to authors
        }.toMap()
        return result
    }
}