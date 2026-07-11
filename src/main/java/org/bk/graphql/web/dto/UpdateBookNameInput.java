package org.bk.graphql.web.dto;

import java.util.UUID;

public record UpdateBookNameInput(UUID id, String name, int version) {
}
