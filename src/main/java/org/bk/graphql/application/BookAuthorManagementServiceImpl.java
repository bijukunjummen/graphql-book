package org.bk.graphql.application;

import org.bk.graphql.application.port.out.AuthorStore;
import org.bk.graphql.application.port.out.BookAuthorLinkStore;
import org.bk.graphql.common.query.ByIds;
import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.BookId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookAuthorManagementServiceImpl implements BookAuthorManagementService {
    private final BookAuthorLinkStore bookAuthorLinkStore;
    private final AuthorStore authorStore;

    public BookAuthorManagementServiceImpl(BookAuthorLinkStore bookAuthorLinkStore, AuthorStore authorStore) {
        this.bookAuthorLinkStore = bookAuthorLinkStore;
        this.authorStore = authorStore;
    }

    @Override
    public Map<BookId, List<Author>> getAuthorsForBooks(ByIds<BookId> ids) {
        Map<BookId, List<AuthorId>> authorIdsForBooks = bookAuthorLinkStore.findAuthorIdsByBookIds(ids.ids());
        List<AuthorId> authorIds = authorIdsForBooks.values().stream()
                .flatMap(List::stream)
                .distinct()
                .toList();
        if (authorIds.isEmpty()) {
            return ids.ids().stream().collect(Collectors.toMap(
                    bookId -> bookId,
                    bookId -> List.of()
            ));
        }
        List<Author> authorsFromDb = authorStore.findAllByIds(authorIds);
        Map<AuthorId, Author> authorsById = authorsFromDb.stream()
                .collect(Collectors.toMap(Author::id, a -> a));

        return ids.ids().stream()
                .collect(Collectors.toMap(
                        bookId -> bookId,
                        bookId -> authorIdsForBooks.getOrDefault(bookId, List.of()).stream()
                                .map(authorsById::get)
                                .filter(Objects::nonNull)
                                .toList()
                ));
    }
}
