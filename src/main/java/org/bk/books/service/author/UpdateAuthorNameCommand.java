package org.bk.books.service.author;

import org.bk.books.domain.AuthorId;

public record UpdateAuthorNameCommand(AuthorId id, String name, int version) {
}

