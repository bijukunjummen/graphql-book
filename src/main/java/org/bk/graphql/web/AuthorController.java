package org.bk.graphql.web;

import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.service.AuthorService;
import org.bk.graphql.service.ById;
import org.bk.graphql.service.CreateAuthorCommand;
import org.bk.graphql.service.UpdateAuthorNameCommand;
import org.bk.graphql.web.dto.AuthorDto;
import org.bk.graphql.web.dto.CreateAuthorInput;
import org.bk.graphql.web.dto.CreateAuthorPayload;
import org.bk.graphql.web.dto.SortInput;
import org.bk.graphql.web.dto.UpdateAuthorNameInput;
import org.bk.graphql.web.dto.UpdateAuthorNamePayload;
import org.bk.graphql.web.pagination.ConnectionPageSupport;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AuthorController {
    private final AuthorService authorService;
    private final ConnectionPageSupport pagination;

    public AuthorController(AuthorService authorService, ConnectionPageSupport pagination) {
        this.authorService = authorService;
        this.pagination = pagination;
    }

    @QueryMapping
    public AuthorDto findAuthorById(@Argument AuthorId id) {
        return AuthorDto.map(authorService.getAuthor(new ById<>(id)));
    }

    @MutationMapping
    public CreateAuthorPayload createAuthor(@Argument CreateAuthorInput input) {
        Author createdAuthor = authorService.createAuthor(new CreateAuthorCommand(input.name()));
        return new CreateAuthorPayload(AuthorDto.map(createdAuthor));
    }

    @MutationMapping
    public UpdateAuthorNamePayload updateAuthorName(@Argument UpdateAuthorNameInput input) {
        Author updatedAuthor = authorService.updateAuthorName(
            new UpdateAuthorNameCommand(input.id(), input.name(), input.version())
        );
        return new UpdateAuthorNamePayload(AuthorDto.map(updatedAuthor));
    }

    @QueryMapping
    public Page<AuthorDto> findAuthors(
        ScrollSubrange subrange,
        @Argument List<SortInput> sort
    ) {
        Page<AuthorDto> page = pagination.page(subrange, sort, pageable -> authorService.getAuthors(pageable), AuthorDto::map);
        return page;
    }
}
