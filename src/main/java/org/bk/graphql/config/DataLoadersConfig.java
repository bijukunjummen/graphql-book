package org.bk.graphql.config;

import org.bk.graphql.service.BookService;
import org.bk.graphql.web.BookAuthorsDataLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoadersConfig {

    @Bean
    public BookAuthorsDataLoader bookAuthorsDataLoader(BookService bookService) {
        return new BookAuthorsDataLoader(bookService);
    }
}

