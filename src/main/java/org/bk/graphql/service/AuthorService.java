package org.bk.graphql.service;

import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthorService {
    Author createAuthor(CreateAuthorCommand createAuthorCommand);
    Author createOrUpdateAuthor(CreateOrUpdateAuthorCommand createOrUpdateAuthorCommand);
    Author updateAuthor(UpdateAuthorCommand command);
    Author updateAuthorName(UpdateAuthorNameCommand command);
    Page<Author> getAuthors(GetAuthorsQuery query);
    Author getAuthor(ById<AuthorId> query);
    List<Author> getAuthors(ByIds<AuthorId> query);
    Page<Author> getAuthors(Pageable pageable);
}

