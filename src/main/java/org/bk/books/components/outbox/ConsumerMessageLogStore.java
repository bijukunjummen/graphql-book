package org.bk.books.components.outbox;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Clock;
import java.util.UUID;

@Component
public class ConsumerMessageLogStore {
    private static final String INSERT_SQL = """
            INSERT INTO consumer_message_log (id, consumer_name, event_id, processed_at)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (consumer_name, event_id) DO NOTHING
            """;
    private static final String PROCESSED_SQL = """
            select count(1) from consumer_message_log where event_id = ? ANd consumer_name = ?;
            """;
    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;

    public ConsumerMessageLogStore(JdbcTemplate jdbcTemplate, Clock clock) {
        this.jdbcTemplate = jdbcTemplate;
        this.clock = clock;
    }

    public boolean markProcessed(String consumerName, UUID eventId) {
        int updated = jdbcTemplate.update(INSERT_SQL, UUID.randomUUID(), consumerName, eventId, Timestamp.from(clock.instant()));
        return updated == 1;
    }

    public boolean alreadyProcessed(UUID eventId, String consumerName) {
        int rows = jdbcTemplate.queryForObject(PROCESSED_SQL, Integer.class, eventId, consumerName);
        return rows > 0;
    }
}
