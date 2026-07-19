CREATE TABLE outbox_events
(
    event_id    UUID        PRIMARY KEY,
    event_type  VARCHAR(255) NOT NULL,
    payload     TEXT        NOT NULL,
    created_at  TIMESTAMP   NOT NULL,
    published_at TIMESTAMP,
    attempts    INT         NOT NULL DEFAULT 0,
    last_error  TEXT
);

CREATE INDEX idx_outbox_events_created_at ON outbox_events (created_at);
CREATE INDEX idx_outbox_events_published_at ON outbox_events (published_at);
