package org.bk.books.domain.event;

import org.bk.books.domain.AuthorId;

public record AuthorRenamedEvent(AuthorId authorId, String name) {
}
