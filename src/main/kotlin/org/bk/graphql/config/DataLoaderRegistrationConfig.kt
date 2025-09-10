package org.bk.graphql.config

import org.bk.graphql.domain.BookId
import org.bk.graphql.web.AuthorsWrapper
import org.bk.graphql.web.BookAuthorsDataLoader
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.BatchLoaderRegistry

@Configuration
class DataLoaderRegistrationConfig(
    private val batchLoaderRegistry: BatchLoaderRegistry,
    private val bookAuthorsDataLoader: BookAuthorsDataLoader

) {
    init {
        batchLoaderRegistry.forTypePair(BookId::class.java, AuthorsWrapper::class.java)
            .registerMappedBatchLoader(bookAuthorsDataLoader)
    }
}