package org.bk.books.config;

import org.bk.books.domain.entity.book.BookId;
import org.bk.books.web.BookAuthorsDataLoader;
import org.bk.books.web.BookAuthorsDataLoader.AuthorsWrapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.BatchLoaderRegistry;

@Configuration
public class DataLoaderRegistrationConfig {
  private final BatchLoaderRegistry batchLoaderRegistry;
  private final BookAuthorsDataLoader bookAuthorsDataLoader;

  public DataLoaderRegistrationConfig(
      BatchLoaderRegistry batchLoaderRegistry, BookAuthorsDataLoader bookAuthorsDataLoader) {
    this.batchLoaderRegistry = batchLoaderRegistry;
    this.bookAuthorsDataLoader = bookAuthorsDataLoader;

    batchLoaderRegistry
        .forTypePair(BookId.class, AuthorsWrapper.class)
        .registerMappedBatchLoader(bookAuthorsDataLoader);
  }
}
