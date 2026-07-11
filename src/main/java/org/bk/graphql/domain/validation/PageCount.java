package org.bk.graphql.domain.validation;

import org.bk.graphql.service.exception.DomainException;

public record PageCount(int value) {
    public static PageCount of(int value) {
        if (value <= 0) {
            throw new DomainException("Page count must be greater than zero");
        }
        return new PageCount(value);
    }
}
