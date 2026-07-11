package org.bk.books.service.bookauthorlink;

import org.bk.books.application.port.out.BookAuthorLinkStore;
import org.bk.books.common.query.ByIds;
import org.bk.books.domain.AuthorId;
import org.bk.books.domain.BookId;
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
