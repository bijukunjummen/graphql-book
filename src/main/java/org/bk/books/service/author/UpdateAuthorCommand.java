package org.bk.books.service.author;

public record UpdateAuthorCommand(String id, String name, int version) {
}

