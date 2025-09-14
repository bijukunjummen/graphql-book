package org.bk.graphql.service

import org.bk.graphql.domain.Author
import org.bk.graphql.domain.AuthorId
import org.bk.graphql.domain.Book
import org.bk.graphql.domain.BookId
import org.bk.graphql.entity.AuthorRef
import org.bk.graphql.entity.BookEntity
import org.bk.graphql.repository.BookRepository
import org.bk.graphql.service.exception.DomainException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID
import java.util.stream.Collectors

@Service
class BookServiceImpl(private val bookRepository: BookRepository, private val authorService: AuthorService) :
    BookService {
    override fun createBook(command: CreateBookCommand): Book {
        val bookId = UUID.randomUUID().toString()

        val book = BookEntity(
            id = bookId,
            name = command.name,
            pageCount = command.pageCount,
            authors = command.authors.map { authorId -> AuthorRef(AggregateReference.to(authorId.id)) }
                .toSet(),
            version = 0
        )
        val savedBook: BookEntity = bookRepository.save(book)
        return savedBook.toModel()
    }

    override fun createOrUpdateBook(command: CreateOrUpdateBookCommand): Book {
        val book = bookRepository.findById(command.id)

        book.ifPresentOrElse(
            {
                if (command.version != 0) {
                    val updatedBook = it.copy(
                        name = command.name,
                        pageCount = command.pageCount,
                        authors = command.authors.map { authorId ->
                            AuthorRef(
                                AggregateReference.to(
                                    authorId.id
                                )
                            )
                        }.toSet(),
                        version = command.version
                    )
                    bookRepository.save(updatedBook)
                }
            },
            {
                val newBook = BookEntity(
                    id = command.id,
                    name = command.name,
                    pageCount = command.pageCount,
                    authors = command.authors.map { authorId ->
                        AuthorRef(
                            AggregateReference.to(
                                authorId.id
                            )
                        )
                    }.toSet(),
                )
                bookRepository.save(newBook)
            })
        return bookRepository.findById(command.id).orElseThrow().toModel()
    }

    override fun updateBook(command: UpdateBookCommand): Book {
        val book = bookRepository.findById(command.id)
            .orElseThrow { DomainException("Book not found") }
        val updatedBook = book.copy(
            name = command.name,
            pageCount = command.pageCount,
            authors = command.authors.map { authorId -> AuthorRef(AggregateReference.to(authorId.id)) }
                .toSet(),
            version = command.version
        )
        val savedBook: BookEntity = bookRepository.save(updatedBook)
        return savedBook.toModel()
    }

    override fun getBooks(query: GetBooksQuery): Page<Book> {
        return bookRepository
            .findAll(Pageable.ofSize(query.size).withPage(query.page))
            .map { it.toModel() }
    }

    override fun getBooks(pageable: Pageable): Page<Book> {
        return bookRepository.findAll(pageable)
            .map { it.toModel() }
    }

    override fun getBook(query: ById<BookId>): Optional<Book> {
        val bookId = query.id
        return bookRepository.findById(bookId.id).map { it.toModel() }
    }

    override fun getBooks(query: ByIds<BookId>): List<Book> {
        return bookRepository.findAllById(query.ids.map { bookId -> bookId.id }).map { it.toModel() }
    }

    override fun getAuthorsForBooks(ids: ByIds<BookId>): Map<BookId, List<Author>> {
        val booksFromDb = getBooks(ids)
        val authorIds: List<AuthorId> = booksFromDb.flatMap { book -> book.authors }
        val authorsFromDb: List<Author> = authorService.getAuthors(ByIds(authorIds))
        val authorsById: Map<AuthorId, Author> =
            authorsFromDb.stream().collect(Collectors.toMap({ a -> a.id }, { a -> a }))
        return booksFromDb.map { book ->
            val bookId = book.id
            val authors: List<Author> = book.authors.map { authorId -> authorsById[authorId]!! }
            bookId to authors
        }.toMap()
    }
}