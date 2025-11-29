package org.bk.graphql.service;

public record UpdateAuthorNameCommand(String id, String name, int version) {
}

