package org.bk.books.config;

import org.bk.books.application.BookAuthorManagementService;
import org.bk.books.web.BookAuthorsDataLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoadersConfig {

    @Bean
    public BookAuthorsDataLoader bookAuthorsDataLoader(BookAuthorManagementService bookAuthorManagementService) {
        return new BookAuthorsDataLoader(bookAuthorManagementService);
    }
}
