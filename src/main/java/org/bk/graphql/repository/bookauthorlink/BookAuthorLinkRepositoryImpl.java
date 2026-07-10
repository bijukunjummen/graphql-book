package org.bk.graphql.repository.bookauthorlink;

import org.bk.graphql.entity.BookAuthorLinkEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.List;

public class BookAuthorLinkRepositoryImpl implements BookAuthorLinkCustomRepository {
    private static final String UPSERT_BATCH_QUERY = """
            INSERT INTO book_author (id, book_id, author_id, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (book_id, author_id) DO UPDATE SET
                updated_at = values(updated_at)
            """;

    private final JdbcTemplate jdbcTemplate;

    public BookAuthorLinkRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void upsertAll(List<BookAuthorLinkEntity> entities) {
        jdbcTemplate.batchUpdate(UPSERT_BATCH_QUERY, entities, entities.size(), (ps, e) ->
        {
            ps.setString(1, e.id());
            ps.setString(2, e.bookId());
            ps.setString(3, e.authorId());
            ps.setTimestamp(4, Timestamp.from(e.createdAt()));
            ps.setTimestamp(5, Timestamp.from(e.updatedAt()));
        });
    }
}
