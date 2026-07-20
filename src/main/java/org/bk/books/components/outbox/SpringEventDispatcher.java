package org.bk.books.components.outbox;

import org.springframework.context.ApplicationEventPublisher;

public class SpringEventDispatcher implements EventDispatcher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringEventDispatcher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public <T> void dispatch(T event) {
        applicationEventPublisher.publishEvent(event);
    }
}
