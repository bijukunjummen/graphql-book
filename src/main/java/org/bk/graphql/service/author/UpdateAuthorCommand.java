package org.bk.graphql.service.author;

public record UpdateAuthorCommand(String id, String name, int version) {
}

