package org.bk.graphql.config;

import org.bk.graphql.domain.BookId;
import org.bk.graphql.web.AuthorsWrapper;
import org.bk.graphql.web.BookAuthorsDataLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.BatchLoaderRegistry;

@Configuration
public class DataLoaderRegistrationConfig {
    private final BatchLoaderRegistry batchLoaderRegistry;
    private final BookAuthorsDataLoader bookAuthorsDataLoader;

    public DataLoaderRegistrationConfig(
        BatchLoaderRegistry batchLoaderRegistry,
        BookAuthorsDataLoader bookAuthorsDataLoader
    ) {
        this.batchLoaderRegistry = batchLoaderRegistry;
        this.bookAuthorsDataLoader = bookAuthorsDataLoader;
        
        batchLoaderRegistry.forTypePair(BookId.class, AuthorsWrapper.class)
            .registerMappedBatchLoader(bookAuthorsDataLoader);
    }
}

