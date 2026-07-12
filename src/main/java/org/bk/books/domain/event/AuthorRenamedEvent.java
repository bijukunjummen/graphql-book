package org.bk.books.domain.event;

import org.bk.books.domain.entity.author.AuthorId;

public record AuthorRenamedEvent(AuthorId authorId, String name) {}
