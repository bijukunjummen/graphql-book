package org.bk.graphql.service

import org.bk.graphql.domain.Author
import org.bk.graphql.domain.AuthorId
import org.bk.graphql.domain.Book
import org.bk.graphql.domain.BookId
import org.bk.graphql.entity.AuthorRef
import org.bk.graphql.entity.BookEntity
import org.bk.graphql.repository.AuthorRepository
import org.bk.graphql.repository.BookRepository
import org.bk.graphql.service.exception.DomainException
import org.bk.graphql.web.dto.AuthorDto
import org.bk.graphql.web.dto.BookDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID
import java.util.stream.Collectors

@Service
class BookServiceImpl(private val bookRepository: BookRepository, private val authorService: AuthorService) : BookService {
    override fun createBook(createBookCommand: CreateBookCommand): Book {
        val bookId = UUID.randomUUID().toString()

        val book = BookEntity(
            id = bookId,
            name = createBookCommand.name,
            pageCount = createBookCommand.pageCount,
            authors = createBookCommand.authors.map { authorId -> AuthorRef(AggregateReference.to(authorId.id)) }.toSet(),
            version = 0
        )
        val savedBook: BookEntity =  bookRepository.save(book)
        return savedBook.toModel()
    }

    override fun createOrUpdateBook(createOrUpdateBookCommand: CreateOrUpdateBookCommand): Book {
        val book = bookRepository.findById(createOrUpdateBookCommand.id)

        book.ifPresentOrElse(
            {
                if (createOrUpdateBookCommand.version != 0) {
                    val updatedBook = it.copy(
                        name = createOrUpdateBookCommand.name,
                        pageCount = createOrUpdateBookCommand.pageCount,
                        authors = createOrUpdateBookCommand.authors.map { authorId -> AuthorRef(AggregateReference.to(authorId.id)) }.toSet(),
                        version = createOrUpdateBookCommand.version
                    )
                    bookRepository.save(updatedBook)
                }
            },
            {
                val newBook = BookEntity(
                    id = createOrUpdateBookCommand.id,
                    name = createOrUpdateBookCommand.name,
                    pageCount = createOrUpdateBookCommand.pageCount,
                    authors = createOrUpdateBookCommand.authors.map { authorId -> AuthorRef(AggregateReference.to(authorId.id)) }.toSet(),
                )
                bookRepository.save(newBook)
            })
        return bookRepository.findById(createOrUpdateBookCommand.id).orElseThrow().toModel()
    }

    override fun updateBook(updateBookCommand: UpdateBookCommand): Book {
        val book = bookRepository.findById(updateBookCommand.id)
            .orElseThrow { DomainException("Book not found") }
        val updatedBook = book.copy(
            name = updateBookCommand.name,
            pageCount = updateBookCommand.pageCount,
            authors = updateBookCommand.authors.map { authorId -> AuthorRef(AggregateReference.to(authorId.id)) }.toSet(),
            version = updateBookCommand.version
        )
        val savedBook: BookEntity =  bookRepository.save(updatedBook)
        return savedBook.toModel()
    }

    override fun getBooks(getBooksQuery: GetBooksQuery): Page<Book> {
        return bookRepository
            .findAll(Pageable.ofSize(getBooksQuery.size).withPage(getBooksQuery.page))
            .map { it.toModel() }
    }

    override fun getBook(byIdQuery: ById<BookId>): Optional<Book> {
        val bookId = byIdQuery.id
        return bookRepository.findById(bookId.id).map { it.toModel() }
    }

    override fun getBooks(byIdQuery: ByIds<BookId>): List<Book> {
        return bookRepository.findAllById(byIdQuery.ids.map { bookId -> bookId.id }).map { it.toModel() }
    }

    override fun getAuthorsForBooks(ids: ByIds<BookId>): Map<BookId, List<Author>> {
        val booksFromDb = getBooks(ids)
        val authorIds: List<AuthorId> = booksFromDb.flatMap { book -> book.authors }
        val authorsFromDb: List<Author> = authorService.getAuthors(ByIds(authorIds))
        val authorsById: Map<AuthorId, Author> = authorsFromDb.stream().collect(Collectors.toMap({ a -> a.id }, { a -> a }))
        return booksFromDb.map { book ->
            val bookId = book.id
            val authors: List<Author> = book.authors.map { authorId -> authorsById[authorId]!! }
            bookId to authors
        }.toMap()
    }
}