package org.bk.graphql.domain.event;

import org.bk.graphql.domain.AuthorId;

public record AuthorRenamedEvent(AuthorId authorId, String name) {
}
