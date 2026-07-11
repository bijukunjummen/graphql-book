package org.bk.books.web.dto;

import java.util.UUID;

public record UpdateBookNameInput(UUID id, String name, int version) {
}
