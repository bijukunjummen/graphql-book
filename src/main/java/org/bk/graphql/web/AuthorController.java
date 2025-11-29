package org.bk.graphql.web;

import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.service.AuthorService;
import org.bk.graphql.service.BookService;
import org.bk.graphql.service.ById;
import org.bk.graphql.service.CreateAuthorCommand;
import org.bk.graphql.service.UpdateAuthorNameCommand;
import org.bk.graphql.web.dto.AuthorDto;
import org.bk.graphql.web.dto.CreateAuthorInput;
import org.bk.graphql.web.dto.CreateAuthorPayload;
import org.bk.graphql.web.dto.OrderField;
import org.bk.graphql.web.dto.SortInput;
import org.bk.graphql.web.dto.UpdateAuthorNameInput;
import org.bk.graphql.web.dto.UpdateAuthorNamePayload;
import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AuthorController {
    private final AuthorService authorService;
    private final BookService bookService;

    public AuthorController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @QueryMapping
    public AuthorDto findAuthorById(@Argument String id) {
        return AuthorDto.map(authorService.getAuthor(new ById<>(new AuthorId(id))));
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
        if (sort == null) {
            sort = List.of(new SortInput("name", OrderField.ASC));
        }
        OffsetScrollPosition scrollPosition = (OffsetScrollPosition) subrange.position()
            .orElse(ScrollPosition.offset());
        int limit = subrange.count().orElse(10);
        int offset = scrollPosition.isInitial() ? 0 : (int) (scrollPosition.getOffset() + 1);
        List<Sort.Order> orderList = sort.stream()
            .map(s -> Sort.Order.by(s.field())
                .with(s.order() == OrderField.ASC ? Sort.Direction.ASC : Sort.Direction.DESC))
            .collect(Collectors.toList());
        Sort sortObj = Sort.by(orderList);
        PageRequest pageable = PageRequest.of(limit != 0 ? offset / limit : 0, limit, sortObj);
        Page<Author> page = authorService.getAuthors(pageable);
        return page.map(AuthorDto::map);
    }
}

