package org.bk.graphql.web.dto;

public record UpdateBookNameInput(String id, String name, int version) {
}
