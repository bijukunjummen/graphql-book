package org.bk.books.entity;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("outbox_events")
public record OutboxEventEntity(
        @Id UUID eventId,
        String eventType,
        String payload,
        Instant createdAt,
        Instant publishedAt,
        int attempts,
        String lastError) {}
