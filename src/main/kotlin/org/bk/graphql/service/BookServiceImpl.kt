package org.bk.graphql.service

import org.bk.graphql.model.Book
import org.bk.graphql.repository.BookRepository
import org.bk.graphql.service.exception.DomainException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BookServiceImpl(private val bookRepository: BookRepository) : BookService {
    override fun createBook(createBookCommand: CreateBookCommand): Book {
        val book = Book(
            id = UUID.randomUUID().toString(),
            name = createBookCommand.name,
            pageCount = createBookCommand.pageCount,
            authorId = createBookCommand.authorId,
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
                        authorId = createOrUpdateBookCommand.authorId,
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
                    authorId = createOrUpdateBookCommand.authorId
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
            authorId = updateBookCommand.authorId,
            version = updateBookCommand.version
        )
        return bookRepository.save(updatedBook)
    }

    override fun getBooks(getBooksQuery: GetBooksQuery): Page<Book> {
        return bookRepository.findAll(Pageable.ofSize(getBooksQuery.size).withPage(getBooksQuery.page))
    }
}