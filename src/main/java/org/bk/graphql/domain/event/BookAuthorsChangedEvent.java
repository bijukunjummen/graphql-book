package org.bk.graphql.domain.event;

import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.BookId;

import java.util.List;

public record BookAuthorsChangedEvent(BookId bookId, List<AuthorId> authorIds) {
}
