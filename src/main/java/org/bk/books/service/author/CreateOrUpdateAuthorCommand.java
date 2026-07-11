package org.bk.books.service.author;

import org.bk.books.domain.AuthorId;

public record CreateOrUpdateAuthorCommand(AuthorId id, String name, int version) {
    public CreateOrUpdateAuthorCommand(AuthorId id, String name) {
        this(id, name, 0);
    }
}

