package org.bk.books.web.dto;

import java.util.UUID;

public record UpdateAuthorNameInput(UUID id, String name, int version) {
}

