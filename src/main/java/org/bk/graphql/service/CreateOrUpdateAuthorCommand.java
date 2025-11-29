package org.bk.graphql.service;

public record CreateOrUpdateAuthorCommand(String id, String name, int version) {
    public CreateOrUpdateAuthorCommand(String id, String name) {
        this(id, name, 0);
    }
}

