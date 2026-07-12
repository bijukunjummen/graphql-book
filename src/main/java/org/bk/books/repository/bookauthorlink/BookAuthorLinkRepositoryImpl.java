package org.bk.books.repository.bookauthorlink;

import java.sql.Timestamp;
import java.util.List;
import org.bk.books.entity.BookAuthorLinkEntity;
import org.springframework.jdbc.core.JdbcTemplate;

public class BookAuthorLinkRepositoryImpl implements BookAuthorLinkCustomRepository {
  private static final String UPSERT_BATCH_QUERY =
      """
            INSERT INTO book_author (id, book_id, author_id, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (book_id, author_id) DO UPDATE SET
                updated_at = EXCLUDED.updated_at
            """;

  private final JdbcTemplate jdbcTemplate;

  public BookAuthorLinkRepositoryImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void upsertAll(List<BookAuthorLinkEntity> entities) {
    jdbcTemplate.batchUpdate(
        UPSERT_BATCH_QUERY,
        entities,
        entities.size(),
        (ps, e) -> {
          ps.setObject(1, e.id());
          ps.setObject(2, e.bookId());
          ps.setObject(3, e.authorId());
          ps.setTimestamp(4, Timestamp.from(e.createdAt()));
          ps.setTimestamp(5, Timestamp.from(e.updatedAt()));
        });
  }
}
