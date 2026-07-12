package org.bk.books.service.author;

import java.util.List;
import org.bk.books.common.query.ById;
import org.bk.books.common.query.ByIds;
import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.service.author.AuthorServiceCommands.CreateAuthorCommand;
import org.bk.books.service.author.AuthorServiceCommands.CreateOrUpdateAuthorCommand;
import org.bk.books.service.author.AuthorServiceCommands.UpdateAuthorNameCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService {
    Author createAuthor(CreateAuthorCommand createAuthorCommand);

    Author createOrUpdateAuthor(CreateOrUpdateAuthorCommand createOrUpdateAuthorCommand);

    Author updateAuthorName(UpdateAuthorNameCommand command);

    Author getAuthor(ById<AuthorId> query);

    List<Author> getAuthors(ByIds<AuthorId> query);

    Page<Author> getAuthors(Pageable pageable);
}
