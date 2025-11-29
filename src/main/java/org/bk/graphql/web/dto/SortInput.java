package org.bk.graphql.web.dto;

public record SortInput(String field, OrderField order) {
    public SortInput(String field) {
        this(field, OrderField.ASC);
    }
}

