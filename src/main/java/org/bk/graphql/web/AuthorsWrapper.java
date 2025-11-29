package org.bk.graphql.web;

import org.bk.graphql.web.dto.AuthorDto;

import java.util.List;

public record AuthorsWrapper(List<AuthorDto> authors) {
}

