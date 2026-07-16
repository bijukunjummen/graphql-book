package org.bk.books.repository.bookauthorlink;

import java.util.List;
import org.bk.books.entity.BookAuthorLinkEntity;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface BookAuthorLinkEntityCustomRepository {
    void upsertAll(List<BookAuthorLinkEntity> entities);
}
