package org.bk.graphql.service.author;

import java.util.UUID;

public record UpdateAuthorNameCommand(UUID id, String name, int version) {
}

