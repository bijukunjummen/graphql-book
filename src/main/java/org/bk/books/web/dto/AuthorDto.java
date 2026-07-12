package org.bk.books.web.dto;

import org.bk.books.domain.entity.author.Author;

public record AuthorDto(String id, String name, int version) {
  public static AuthorDto map(Author author) {
    return new AuthorDto(author.id().id().toString(), author.name(), author.version());
  }
}
