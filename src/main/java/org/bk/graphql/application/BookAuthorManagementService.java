package org.bk.graphql.application;

import org.bk.graphql.common.query.ByIds;
import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.BookId;

import java.util.List;
import java.util.Map;

public interface BookAuthorManagementService {
    Map<BookId, List<Author>> getAuthorsForBooks(ByIds<BookId> ids) ;
}
