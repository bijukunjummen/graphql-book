package org.bk.graphql.service.author;

public record UpdateAuthorNameCommand(String id, String name, int version) {
}

