package org.bk.graphql.web

import org.bk.graphql.domain.AuthorId
import org.bk.graphql.domain.Book
import org.bk.graphql.domain.BookId
import org.bk.graphql.service.BookService
import org.bk.graphql.service.ById
import org.bk.graphql.service.CreateBookCommand
import org.bk.graphql.web.dto.BookDto
import org.bk.graphql.web.dto.CreateBookInput
import org.bk.graphql.web.dto.CreateBookPayload
import org.bk.graphql.web.dto.OrderField
import org.bk.graphql.web.dto.SortInput
import org.springframework.data.domain.OffsetScrollPosition
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.ScrollPosition
import org.springframework.data.domain.Sort
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.graphql.data.query.ScrollSubrange
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import java.time.Duration

@Controller
class BookController(private val bookService: BookService) {

    @QueryMapping
    fun findBookById(@Argument id: String): BookDto {
        return BookDto.map(bookService.getBook(ById(BookId(id))).orElseThrow())
    }

    @MutationMapping
    fun createBook(@Argument input: CreateBookInput): CreateBookPayload {
        val createdBook: Book = bookService.createBook(
            CreateBookCommand(
                name = input.name,
                pageCount = input.pageCount,
                authors = input.authors.map { id -> AuthorId(id) }.toSet()
            )
        )
        return CreateBookPayload(BookDto.map(createdBook))
    }

    @SubscriptionMapping
    fun getABook(@Argument id: String): Flux<BookDto> {
        return Flux.interval(Duration.ofSeconds(5))
            .map { l ->  BookDto.map(bookService.getBook(ById(BookId(id))).orElseThrow())}

    }

    @QueryMapping
    fun findBooks(
        subrange: ScrollSubrange,
        @Argument sort: List<SortInput>? = listOf(SortInput("name", OrderField.ASC))
    ): Page<BookDto> {
        val scrollPosition: OffsetScrollPosition =
            subrange.position().orElse(ScrollPosition.offset()) as OffsetScrollPosition
        val limit = subrange.count().orElse(10)
        val offset = if (scrollPosition.isInitial) 0 else scrollPosition.offset.plus(1).toInt()
        val orderList: List<Sort.Order> = sort?.map { sort ->
            Sort.Order.by(sort.field).with(if (sort.order == OrderField.ASC) Sort.Direction.ASC else Sort.Direction.DESC)
        } ?: emptyList()
        val sort = Sort.by(orderList)
        val pageable = PageRequest.of(if (limit != 0) offset / limit else 0, limit, sort)
        val page: Page<Book> = bookService.getBooks(pageable)
        return page.map { book -> BookDto.map(book) }
    }
}