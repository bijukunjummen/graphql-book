package org.bk.graphql.service.bookauthorlink;

import org.bk.graphql.application.port.out.BookAuthorLinkStore;
import org.bk.graphql.common.query.ByIds;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.BookId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BookAuthorLinkServiceImpl implements BookAuthorLinkService {
    private final BookAuthorLinkStore bookAuthorLinkStore;

    public BookAuthorLinkServiceImpl(BookAuthorLinkStore bookAuthorLinkStore) {
        this.bookAuthorLinkStore = bookAuthorLinkStore;
    }

    @Override
    public Map<BookId, List<AuthorId>> getAuthorIdsForBooks(ByIds<BookId> query) {
        return bookAuthorLinkStore.findAuthorIdsByBookIds(query.ids());
    }
}
