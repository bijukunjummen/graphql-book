package org.bk.graphql.service.author;

import java.util.UUID;

public record CreateOrUpdateAuthorCommand(UUID id, String name, int version) {
    public CreateOrUpdateAuthorCommand(UUID id, String name) {
        this(id, name, 0);
    }
}

