package org.bk.books.components.outbox;

public record Envelope(String event, String eventType) {}
