package org.bk.graphql.service

import org.bk.graphql.model.AuthorRef
import org.bk.graphql.model.Book
import org.bk.graphql.repository.BookRepository
import org.bk.graphql.service.exception.DomainException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID

@Service
class BookServiceImpl(private val bookRepository: BookRepository) : BookService {
    override fun createBook(createBookCommand: CreateBookCommand): Book {
        val bookId = UUID.randomUUID().toString()

        val book = Book(
            id = bookId,
            name = createBookCommand.name,
            pageCount = createBookCommand.pageCount,
            authors = createBookCommand.authors.map { authorId -> AuthorRef(AggregateReference.to(authorId)) }.toSet(),
            version = 0
        )
        return bookRepository.save(book)
    }

    override fun createOrUpdateBook(createOrUpdateBookCommand: CreateOrUpdateBookCommand): Book {
        val book = bookRepository.findById(createOrUpdateBookCommand.id)

        book.ifPresentOrElse(
            {
                if (createOrUpdateBookCommand.version != 0) {
                    val updatedBook = it.copy(
                        name = createOrUpdateBookCommand.name,
                        pageCount = createOrUpdateBookCommand.pageCount,
                        authors = createOrUpdateBookCommand.authors.map { authorId -> AuthorRef(AggregateReference.to(authorId)) }.toSet(),
                        version = createOrUpdateBookCommand.version
                    )
                    bookRepository.save(updatedBook)
                }
            },
            {
                val newBook = Book(
                    id = createOrUpdateBookCommand.id,
                    name = createOrUpdateBookCommand.name,
                    pageCount = createOrUpdateBookCommand.pageCount,
                    authors = createOrUpdateBookCommand.authors.map { authorId -> AuthorRef(AggregateReference.to(authorId)) }.toSet(),
                )
                bookRepository.save(newBook)
            })
        return bookRepository.findById(createOrUpdateBookCommand.id).orElseThrow()
    }

    override fun updateBook(updateBookCommand: UpdateBookCommand): Book {
        val book = bookRepository.findById(updateBookCommand.id)
            .orElseThrow { DomainException("Book not found") }
        val updatedBook = book.copy(
            name = updateBookCommand.name,
            pageCount = updateBookCommand.pageCount,
            authors = updateBookCommand.authors.map { authorId -> AuthorRef(AggregateReference.to(authorId)) }.toSet(),
            version = updateBookCommand.version
        )
        return bookRepository.save(updatedBook)
    }

    override fun getBooks(getBooksQuery: GetBooksQuery): Page<Book> {
        return bookRepository.findAll(Pageable.ofSize(getBooksQuery.size).withPage(getBooksQuery.page))
    }

    override fun getBook(byIdQuery: ById): Optional<Book> {
        return bookRepository.findById(byIdQuery.id)
    }
}