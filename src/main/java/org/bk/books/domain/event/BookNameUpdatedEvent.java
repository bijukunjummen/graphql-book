package org.bk.books.domain.event;

import org.bk.books.domain.entity.book.BookId;

public record BookNameUpdatedEvent(BookId bookId, String oldName, String newName) {}
