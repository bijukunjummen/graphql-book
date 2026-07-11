package org.bk.books.outbound.persistence;

import org.bk.books.application.port.out.BookAuthorLinkStore;
import org.bk.books.domain.AuthorId;
import org.bk.books.domain.BookId;
import org.bk.books.entity.BookAuthorLinkEntity;
import org.bk.books.repository.bookauthorlink.BookAuthorLinkRepository;
import org.bk.books.util.Uuids;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class SpringDataBookAuthorLinkStore implements BookAuthorLinkStore {
    private final BookAuthorLinkRepository bookAuthorLinkRepository;
    private final Uuids uuids;

    public SpringDataBookAuthorLinkStore(BookAuthorLinkRepository bookAuthorLinkRepository, Uuids uuids) {
        this.bookAuthorLinkRepository = bookAuthorLinkRepository;
        this.uuids = uuids;
    }

    @Override
    public void replaceAuthorsForBook(BookId bookId, Set<AuthorId> authorIds, Instant now) {
        if (authorIds.isEmpty()) {
            bookAuthorLinkRepository.deleteByBookId(bookId.id());
            return;
        }

        Set<UUID> authorIdsAsUuids = authorIds.stream().map(AuthorId::id).collect(Collectors.toSet());
        bookAuthorLinkRepository.deleteByBookIdAndAuthorIdNotIn(bookId.id(), authorIdsAsUuids);

        List<BookAuthorLinkEntity> links = authorIdsAsUuids.stream()
                .map(authorId -> new BookAuthorLinkEntity(uuids.generateUuid(), bookId.id(), authorId, now, now))
                .toList();
        bookAuthorLinkRepository.upsertAll(links);
    }

    @Override
    public Map<BookId, List<AuthorId>> findAuthorIdsByBookIds(List<BookId> bookIds) {
        Set<UUID> bookIdValues = bookIds.stream().map(BookId::id).collect(Collectors.toSet());
        List<BookAuthorLinkEntity> links = bookAuthorLinkRepository.findAllByBookIdIn(bookIdValues);

        Map<BookId, List<AuthorId>> authorIdsByBookId = new LinkedHashMap<>();
        for (BookAuthorLinkEntity link : links) {
            BookId bookId = BookId.of(link.bookId());
            List<AuthorId> authorIds = authorIdsByBookId.computeIfAbsent(bookId, _ -> new ArrayList<>());
            authorIds.add(AuthorId.of(link.authorId()));
        }
        return authorIdsByBookId;
    }
}
