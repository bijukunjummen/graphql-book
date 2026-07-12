package org.bk.books.service.author;

import org.bk.books.domain.entity.author.AuthorId;

public interface AuthorServiceCommands {
  record CreateAuthorCommand(String name) {}

  record CreateOrUpdateAuthorCommand(AuthorId id, String name, int version) {
    public CreateOrUpdateAuthorCommand(AuthorId id, String name) {
      this(id, name, 0);
    }
  }

  record UpdateAuthorNameCommand(AuthorId id, String name, int version) {}
}
