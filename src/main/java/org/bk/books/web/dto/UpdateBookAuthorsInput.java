package org.bk.books.web.dto;

import java.util.List;

public record UpdateBookAuthorsInput(String id, List<String> authors, int version) {}
