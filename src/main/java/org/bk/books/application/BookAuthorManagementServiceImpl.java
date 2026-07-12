package org.bk.books.application;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bk.books.common.query.ByIds;
import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.BookId;
import org.bk.books.service.author.AuthorService;
import org.bk.books.service.book.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookAuthorManagementServiceImpl implements BookAuthorManagementService {
  private final BookService bookService;
  private final AuthorService authorService;

  public BookAuthorManagementServiceImpl(BookService bookService, AuthorService authorService) {
    this.bookService = bookService;
    this.authorService = authorService;
  }

  @Override
  public Map<BookId, List<Author>> getAuthorsForBooks(ByIds<BookId> ids) {
    Map<BookId, List<AuthorId>> authorIdsForBooks = bookService.getAuthorIdsForBooks(ids);
    List<AuthorId> authorIds =
        authorIdsForBooks.values().stream().flatMap(List::stream).distinct().toList();
    if (authorIds.isEmpty()) {
      return ids.ids().stream().collect(Collectors.toMap(bookId -> bookId, bookId -> List.of()));
    }
    List<Author> authorsFromDb = authorService.getAuthors(new ByIds<>(authorIds));
    Map<AuthorId, Author> authorsById =
        authorsFromDb.stream().collect(Collectors.toMap(Author::id, a -> a));

    return ids.ids().stream()
        .collect(
            Collectors.toMap(
                bookId -> bookId,
                bookId ->
                    authorIdsForBooks.getOrDefault(bookId, List.of()).stream()
                        .map(authorsById::get)
                        .filter(Objects::nonNull)
                        .toList()));
  }
}
