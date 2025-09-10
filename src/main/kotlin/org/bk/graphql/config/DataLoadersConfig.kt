package org.bk.graphql.config

import org.bk.graphql.service.BookService
import org.bk.graphql.web.BookAuthorsDataLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataLoadersConfig {

    @Bean
    fun bookAuthorsDataLoader(bookService: BookService): BookAuthorsDataLoader {
        return BookAuthorsDataLoader(bookService)
    }
}