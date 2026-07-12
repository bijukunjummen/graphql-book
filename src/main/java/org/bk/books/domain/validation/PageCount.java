package org.bk.books.domain.validation;

import org.bk.books.service.exception.DomainException;

public record PageCount(int value) {
  public static PageCount of(int value) {
    if (value <= 0) {
      throw new DomainException("Page count must be greater than zero");
    }
    return new PageCount(value);
  }
}
