package org.bk.graphql.web

import org.bk.graphql.domain.Author
import org.bk.graphql.domain.BookId
import org.bk.graphql.service.BookService
import org.bk.graphql.service.ByIds
import org.bk.graphql.web.dto.AuthorDto
import org.dataloader.BatchLoaderEnvironment
import reactor.core.publisher.Mono
import java.util.function.BiFunction

class BookAuthorsDataLoader(private val bookService: BookService): BiFunction<Set<BookId>, BatchLoaderEnvironment, Mono<Map<BookId, AuthorsWrapper>>> {
    override fun apply(
        bookIds: Set<BookId>,
        u: BatchLoaderEnvironment
    ): Mono<Map<BookId, AuthorsWrapper>> {
        return Mono.fromSupplier {
            val authorsForBooks: Map<BookId, List<Author>> = bookService.getAuthorsForBooks(ByIds(bookIds.toList()))
            authorsForBooks.mapValues { entry ->
                val authorDtos = entry.value.map { author -> AuthorDto.map(author) }
                AuthorsWrapper(authorDtos)
            }
        }
    }
}

data class AuthorsWrapper(val authors: List<AuthorDto>)