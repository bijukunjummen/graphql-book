package org.bk.books.repository.bookauthorlink;

import java.util.List;
import org.bk.books.entity.BookAuthorLinkEntity;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface BookAuthorLinkCustomRepository {
  void upsertAll(List<BookAuthorLinkEntity> entities);
}
