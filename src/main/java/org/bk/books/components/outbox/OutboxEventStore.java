package org.bk.books.components.outbox;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.bk.books.database.repository.entity.OutboxEventEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventStore {
    private static final String INSERT_SQL = """
            INSERT INTO outbox_events (event_id, event_type, payload, created_at, attempts)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (event_id) DO NOTHING
            """;
    private static final String FETCH_PENDING_SQL = """
            SELECT event_id, event_type, payload, created_at, published_at, attempts, last_error
            FROM outbox_events
            WHERE published_at IS NULL
            ORDER BY created_at
            LIMIT ?
            FOR UPDATE SKIP LOCKED
            """;
    private static final String MARK_PUBLISHED_SQL = """
            UPDATE outbox_events
            SET published_at = ?, attempts = attempts + 1, last_error = NULL
            WHERE event_id = ?
            """;
    private static final String RECORD_FAILURE_SQL = """
            UPDATE outbox_events
            SET attempts = attempts + 1, last_error = ?
            WHERE event_id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;

    public OutboxEventStore(JdbcTemplate jdbcTemplate, Clock clock) {
        this.jdbcTemplate = jdbcTemplate;
        this.clock = clock;
    }

    public void append(UUID eventId, String eventType, String payload) {
        Instant now = clock.instant();
        jdbcTemplate.update(INSERT_SQL, eventId, eventType, payload, Timestamp.from(now), 0);
    }

    public List<OutboxEventEntity> fetchPending(int limit) {
        return jdbcTemplate.query(
                FETCH_PENDING_SQL,
                (rs, rowNum) -> new OutboxEventEntity(
                        rs.getObject("event_id", UUID.class),
                        rs.getString("event_type"),
                        rs.getString("payload"),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("published_at") == null
                                ? null
                                : rs.getTimestamp("published_at").toInstant(),
                        rs.getInt("attempts"),
                        rs.getString("last_error")),
                limit);
    }

    public void markPublished(UUID outboxId) {
        jdbcTemplate.update(MARK_PUBLISHED_SQL, Timestamp.from(clock.instant()), outboxId);
    }

    public void recordFailure(UUID outboxId, String errorMessage) {
        jdbcTemplate.update(RECORD_FAILURE_SQL, truncateError(errorMessage), outboxId);
    }

    private String truncateError(String errorMessage) {
        if (errorMessage == null) {
            return "Unknown publish error";
        }
        int maxLength = 1000;
        return errorMessage.length() > maxLength ? errorMessage.substring(0, maxLength) : errorMessage;
    }
}
