package org.bk.graphql.web

import org.bk.graphql.domain.Author
import org.bk.graphql.domain.AuthorId
import org.bk.graphql.service.AuthorService
import org.bk.graphql.service.BookService
import org.bk.graphql.service.ById
import org.bk.graphql.service.CreateAuthorCommand
import org.bk.graphql.service.UpdateAuthorNameCommand
import org.bk.graphql.web.dto.AuthorDto
import org.bk.graphql.web.dto.CreateAuthorInput
import org.bk.graphql.web.dto.CreateAuthorPayload
import org.bk.graphql.web.dto.OrderField
import org.bk.graphql.web.dto.SortInput
import org.bk.graphql.web.dto.UpdateAuthorNameInput
import org.bk.graphql.web.dto.UpdateAuthorNamePayload
import org.springframework.data.domain.OffsetScrollPosition
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.ScrollPosition
import org.springframework.data.domain.Sort
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.query.ScrollSubrange
import org.springframework.stereotype.Controller

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

    @MutationMapping
    fun updateAuthorName(@Argument input: UpdateAuthorNameInput): UpdateAuthorNamePayload {
        val updatedAuthor = authorService.updateAuthorName(UpdateAuthorNameCommand(id = input.id, input.name, input.version))
        return UpdateAuthorNamePayload(AuthorDto.map(updatedAuthor))
    }

    @QueryMapping
    fun findAuthors(
        subrange: ScrollSubrange,
        @Argument sort: List<SortInput>? = listOf(SortInput("name", OrderField.ASC))
    ): Page<AuthorDto> {
        val scrollPosition: OffsetScrollPosition =
            subrange.position().orElse(ScrollPosition.offset()) as OffsetScrollPosition
        val limit = subrange.count().orElse(10)
        val offset = if (scrollPosition.isInitial) 0 else scrollPosition.offset.plus(1).toInt()
        val orderList: List<Sort.Order> = sort?.map { sort ->
            Sort.Order.by(sort.field)
                .with(if (sort.order == OrderField.ASC) Sort.Direction.ASC else Sort.Direction.DESC)
        } ?: emptyList()
        val sort = Sort.by(orderList)
        val pageable = PageRequest.of(if (limit != 0) offset / limit else 0, limit, sort)
        val page: Page<Author> = authorService.getAuthors(pageable)
        return page.map { author -> AuthorDto.map(author) }
    }
}

