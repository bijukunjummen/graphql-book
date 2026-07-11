package org.bk.books.repository.bookauthorlink;

import org.bk.books.entity.BookAuthorLinkEntity;

import java.util.List;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface BookAuthorLinkCustomRepository {
    void upsertAll(List<BookAuthorLinkEntity> entities);
}
