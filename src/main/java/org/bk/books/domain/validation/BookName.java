package org.bk.books.domain.validation;

import org.bk.books.service.exception.DomainException;

public record BookName(String value) {
  public static BookName of(String value) {
    if (value == null || value.isBlank()) {
      throw new DomainException("Book name must not be blank");
    }
    return new BookName(value.trim());
  }
}
