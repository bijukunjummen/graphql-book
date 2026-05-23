package org.bk.graphql.service;

public record UpdateBookNameCommand(String id, String name, int version) {
}
