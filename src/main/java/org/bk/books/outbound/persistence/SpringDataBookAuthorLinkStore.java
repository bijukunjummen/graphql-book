package org.bk.books.outbound.persistence;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.BookId;
import org.bk.books.database.repository.entity.BookAuthorLinkEntity;
import org.bk.books.port.BookAuthorLinkStore;
import org.bk.books.repository.bookauthorlink.BookAuthorLinkEntityRepository;
import org.bk.books.util.Uuids;
import org.springframework.stereotype.Component;

@Component
public class SpringDataBookAuthorLinkStore implements BookAuthorLinkStore {
    private final BookAuthorLinkEntityRepository bookAuthorLinkEntityRepository;
    private final Uuids uuids;

    public SpringDataBookAuthorLinkStore(BookAuthorLinkEntityRepository bookAuthorLinkEntityRepository, Uuids uuids) {
        this.bookAuthorLinkEntityRepository = bookAuthorLinkEntityRepository;
        this.uuids = uuids;
    }

    @Override
    public void replaceAuthorsForBook(BookId bookId, List<AuthorId> authorIds, Instant now) {
        bookAuthorLinkEntityRepository.deleteByBookId(bookId.id());
        if (authorIds.isEmpty()) {
            return;
        }

        List<BookAuthorLinkEntity> links = authorIds.stream()
                .map(authorId -> new BookAuthorLinkEntity(uuids.generateUuid(), bookId.id(), authorId.id(), now, now))
                .toList();
        bookAuthorLinkEntityRepository.upsertAll(links);
    }

    @Override
    public Map<BookId, List<AuthorId>> findAuthorIdsByBookIds(List<BookId> bookIds) {
        Set<UUID> bookIdValues = bookIds.stream().map(BookId::id).collect(Collectors.toSet());
        List<BookAuthorLinkEntity> links = bookAuthorLinkEntityRepository.findAllByBookIdIn(bookIdValues);

        Map<BookId, List<AuthorId>> authorIdsByBookId = new LinkedHashMap<>();
        for (BookAuthorLinkEntity link : links) {
            BookId bookId = BookId.of(link.bookId());
            List<AuthorId> authorIds = authorIdsByBookId.computeIfAbsent(bookId, _ -> new ArrayList<>());
            authorIds.add(AuthorId.of(link.authorId()));
        }
        return authorIdsByBookId;
    }
}
