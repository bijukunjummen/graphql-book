CREATE TABLE consumer_message_log
(
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL,
    consumer_name VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP NOT NULL,
    CONSTRAINT unq_event_id_consumer UNIQUE (event_id, consumer_name)
);

CREATE INDEX idx_consumer_message_log_processed_at ON consumer_message_log (processed_at);
