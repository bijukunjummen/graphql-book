package org.bk.graphql.service;

public record UpdateAuthorCommand(String id, String name, int version) {
}

