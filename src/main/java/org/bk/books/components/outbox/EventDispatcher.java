package org.bk.books.components.outbox;

public interface EventDispatcher {
    <T> void dispatch(T event);
}
