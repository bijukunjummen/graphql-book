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
import java.util.UUID;
import java.util.stream.Collectors;

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
        Set<UUID> newAuthorIds = authorIds.stream().map(AuthorId::id).collect(Collectors.toSet());
        UUID bookIdValue = bookId.id();

        List<BookAuthorLinkEntity> bookAuthorEntities = bookAuthorLinkRepository.findAllByBookIdIn(Set.of(bookIdValue));
        List<UUID> existingAuthorIds = bookAuthorEntities.stream()
                .map(BookAuthorLinkEntity::authorId)
                .toList();
        List<BookAuthorLinkEntity> toDelete = bookAuthorEntities.stream()
                .filter(e -> !newAuthorIds.contains(e.authorId()))
                .toList();
        Instant now = clock.instant();
        List<BookAuthorLinkEntity> toAdd = authorIds
                .stream()
                .filter(authorId -> !existingAuthorIds.contains(authorId.id()))
                .map(authorId -> new BookAuthorLinkEntity(uuids.generateUuid(), bookIdValue, authorId.id(), now, now))
                .toList();
        bookAuthorLinkRepository.deleteAll(toDelete);
        bookAuthorLinkRepository.upsertAll(toAdd);
    }

    @Override
    public Map<BookId, List<AuthorId>> getAuthorIdsForBooks(ByIds<BookId> query) {
        Set<UUID> bookIdValues = query.ids().stream()
                .map(BookId::id)
                .collect(Collectors.toSet());

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
