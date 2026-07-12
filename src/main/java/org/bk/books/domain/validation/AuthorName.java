package org.bk.books.domain.validation;

import org.bk.books.service.exception.DomainException;

public record AuthorName(String value) {
  public static AuthorName of(String value) {
    if (value == null || value.isBlank()) {
      throw new DomainException("Author name must not be blank");
    }
    return new AuthorName(value.trim());
  }
}
