package org.bk.graphql.service.bookauthorlink;

import org.bk.graphql.common.query.ByIds;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.BookId;
import org.bk.graphql.entity.BookAuthorLinkEntity;
import org.bk.graphql.repository.bookauthorlink.BookAuthorLinkRepository;
import org.bk.graphql.util.Uuids;
import org.springframework.stereotype.Service;

import java.time.Clock;
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
