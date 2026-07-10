package org.bk.graphql.repository.bookauthorlink;

import org.bk.graphql.entity.BookAuthorLinkEntity;

import java.util.List;


public interface BookAuthorLinkCustomRepository {
    void upsertAll(List<BookAuthorLinkEntity> entities);
}
