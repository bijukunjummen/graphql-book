package org.bk.graphql.service.bookauthorlink;

import org.bk.graphql.common.query.ByIds;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.BookId;
import org.bk.graphql.entity.BookAuthorLinkEntity;
import org.bk.graphql.repository.bookauthorlink.BookAuthorLinkRepository;
import org.bk.graphql.util.Uuids;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BookAuthorLinkServiceImpl implements BookAuthorLinkService {
    private final BookAuthorLinkRepository bookAuthorLinkRepository;
    private final Clock clock;
    private final Uuids uuids;

    public BookAuthorLinkServiceImpl(
            BookAuthorLinkRepository bookAuthorLinkRepository,
            Clock clock,
            Uuids uuids
    ) {
        this.bookAuthorLinkRepository = bookAuthorLinkRepository;
        this.clock = clock;
        this.uuids = uuids;
    }

    @Transactional
    @Override
    public void replaceAuthorsForBook(BookId bookId, Set<AuthorId> authorIds) {
        String bookIdValue = bookId.id().toString();

        List<BookAuthorLinkEntity> bookAuthorEntities = bookAuthorLinkRepository.findAllByBookIdIn(Set.of(bookIdValue));
        List<String> existingAuthorIds = bookAuthorEntities.stream()
                .map(e -> e.authorId())
                .toList();
        List<BookAuthorLinkEntity> toDelete = bookAuthorEntities.stream()
                .filter(e -> !authorIds.contains(e.authorId()))
                .toList();
        Instant now = clock.instant();
        List<BookAuthorLinkEntity> toAdd = authorIds
                .stream()
                .filter(authorId -> !existingAuthorIds.contains(authorId.toString()))
                .map(authorId -> new BookAuthorLinkEntity(uuids.generateUuid().toString(), bookIdValue, authorId.id().toString(), now, now))
                .toList();
        bookAuthorLinkRepository.deleteAll(toDelete);
        bookAuthorLinkRepository.upsertAll(toAdd);
    }

    @Override
    public Map<BookId, List<AuthorId>> getAuthorIdsForBooks(ByIds<BookId> query) {
        Set<String> bookIdValues = query.ids().stream()
                .map(bookId -> bookId.id().toString())
                .collect(java.util.stream.Collectors.toSet());

        List<BookAuthorLinkEntity> links = bookAuthorLinkRepository.findAllByBookIdIn(bookIdValues);
        Map<BookId, List<AuthorId>> authorIdsByBookId = new LinkedHashMap<>();
        for (BookAuthorLinkEntity link : links) {
            BookId bookId = BookId.parse(link.bookId());
            List<AuthorId> authorIds = authorIdsByBookId.computeIfAbsent(bookId, unused -> new ArrayList<>());
            authorIds.add(AuthorId.parse(link.authorId()));
        }
        return authorIdsByBookId;
    }
}
