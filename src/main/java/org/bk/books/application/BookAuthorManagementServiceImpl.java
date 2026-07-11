package org.bk.books.application;

import org.bk.books.common.query.ByIds;
import org.bk.books.domain.Author;
import org.bk.books.domain.AuthorId;
import org.bk.books.domain.BookId;
import org.bk.books.service.author.AuthorService;
import org.bk.books.service.bookauthorlink.BookAuthorLinkService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookAuthorManagementServiceImpl implements BookAuthorManagementService {
    private final BookAuthorLinkService bookAuthorLinkService;
    private final AuthorService authorService;

    public BookAuthorManagementServiceImpl(BookAuthorLinkService bookAuthorLinkService, AuthorService authorService) {
        this.bookAuthorLinkService = bookAuthorLinkService;
        this.authorService = authorService;
    }

    @Override
    public Map<BookId, List<Author>> getAuthorsForBooks(ByIds<BookId> ids) {
        Map<BookId, List<AuthorId>> authorIdsForBooks = bookAuthorLinkService.getAuthorIdsForBooks(ids);
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
        List<Author> authorsFromDb = authorService.getAuthors(new ByIds<>(authorIds));
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
