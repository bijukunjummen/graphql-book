package org.bk.books.components.outbox;

import org.bk.books.domain.entity.common.BaseEvent;

public interface OutboxMessagePublisher {
    void publish(BaseEvent message);
}
