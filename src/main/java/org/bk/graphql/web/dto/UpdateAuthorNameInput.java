package org.bk.graphql.web.dto;

import java.util.UUID;

public record UpdateAuthorNameInput(UUID id, String name, int version) {
}

