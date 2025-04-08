package org.bk.graphql.web

import org.bk.graphql.db.Book
import org.bk.graphql.repository.BookRepository
import org.bk.graphql.web.dto.*
import org.springframework.data.domain.OffsetScrollPosition
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.ScrollPosition
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Order
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.pagination.EncodingCursorStrategy
import org.springframework.graphql.data.query.ScrollSubrange
import org.springframework.stereotype.Controller

@Controller
class BookController(private val bookRepository: BookRepository, private val cursorStrategy: EncodingCursorStrategy<ScrollPosition>) {

    @QueryMapping
    fun findBookById(@Argument id: String): BookDto {
        return BookDto.map(bookRepository.findById(id).orElseThrow())
    }

    @QueryMapping
    fun allBooks(subrange: ScrollSubrange, @Argument sort: List<SortInput>? = listOf(SortInput("name", OrderField.ASC))): Page<Book> {
        val scrollPosition:OffsetScrollPosition = subrange.position().orElse(ScrollPosition.offset()) as OffsetScrollPosition
        val limit = subrange.count().orElse(10)
        val offset = if (scrollPosition.isInitial) 0 else scrollPosition.offset.plus(1).toInt()
        val orderList: List<Order> = sort?.map { sort ->
            Order.by(sort.field).with(if (sort.order == OrderField.ASC) Sort.Direction.ASC else Sort.Direction.DESC)
        } ?: emptyList()
        val sort = Sort.by(orderList)
        val pageable = PageRequest.of(if (limit != 0) offset / limit else 0, limit, sort)
        return bookRepository.findAll(pageable)
    }
}