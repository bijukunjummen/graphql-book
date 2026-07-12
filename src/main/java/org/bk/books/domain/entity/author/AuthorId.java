package org.bk.books.domain.entity.author;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;

public record AuthorId(UUID id) {
  @JsonCreator
  public static AuthorId parse(String id) {
    return new AuthorId(UUID.fromString(id));
  }

  public static AuthorId of(UUID id) {
    return new AuthorId(id);
  }
}
