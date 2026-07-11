package org.bk.graphql.application;

import org.bk.graphql.TimeTestData;
import org.bk.graphql.application.port.out.AuthorStore;
import org.bk.graphql.application.port.out.BookAuthorLinkStore;
import org.bk.graphql.common.query.ByIds;
import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.BookId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookAuthorManagementServiceTest {
    @InjectMocks
    private BookAuthorManagementServiceImpl bookAuthorManagementService;

    @Mock
    private BookAuthorLinkStore bookAuthorLinkStore;

    @Mock
    private AuthorStore authorStore;

    @Test
    void test_getAuthorsForBooks_whenNoAuthorLinks_returnsEmptyListsAndSkipsAuthorFetch() {
        BookId firstBookId = BookId.parse("8ac017c8-fafb-4f6c-88fe-59651bdc04b8");
        BookId secondBookId = BookId.parse("c22ee984-7f74-4158-8bd5-79235b0ad051");
        ByIds<BookId> query = new ByIds<>(List.of(firstBookId, secondBookId));

        when(bookAuthorLinkStore.findAuthorIdsByBookIds(query.ids())).thenReturn(Map.of());

        Map<BookId, List<Author>> result = bookAuthorManagementService.getAuthorsForBooks(query);

        assertThat(result)
                .containsEntry(firstBookId, List.of())
                .containsEntry(secondBookId, List.of());
        verify(authorStore, never()).findAllByIds(org.mockito.ArgumentMatchers.<List<AuthorId>>any());
    }

    @Test
    void test_getAuthorsForBooks_whenLinksPresent_returnsMappedAuthorsPerBook() {
        BookId firstBookId = BookId.parse("8ac017c8-fafb-4f6c-88fe-59651bdc04b8");
        BookId secondBookId = BookId.parse("c22ee984-7f74-4158-8bd5-79235b0ad051");
        AuthorId firstAuthorId = AuthorId.parse("d50657f5-5e00-4117-ab97-e6a45e33e444");
        AuthorId secondAuthorId = AuthorId.parse("12c2d97c-4654-4398-bc0e-c40cf96715c6");
        ByIds<BookId> query = new ByIds<>(List.of(firstBookId, secondBookId));

        Author firstAuthor = Author.create(firstAuthorId, "George Orwell",
                TimeTestData.DEFAULT_CREATED_DATE, TimeTestData.DEFAULT_UPDATED_DATE, 1);
        Author secondAuthor = Author.create(secondAuthorId, "Aldous Huxley",
                TimeTestData.DEFAULT_CREATED_DATE, TimeTestData.DEFAULT_UPDATED_DATE, 1);

        when(bookAuthorLinkStore.findAuthorIdsByBookIds(query.ids())).thenReturn(Map.of(
                firstBookId, List.of(firstAuthorId),
                secondBookId, List.of(secondAuthorId)
        ));
        when(authorStore.findAllByIds(org.mockito.ArgumentMatchers.<List<AuthorId>>any()))
                .thenReturn(List.of(firstAuthor, secondAuthor));

        Map<BookId, List<Author>> result = bookAuthorManagementService.getAuthorsForBooks(query);

        assertThat(result)
                .containsEntry(firstBookId, List.of(firstAuthor))
                .containsEntry(secondBookId, List.of(secondAuthor));
        verify(authorStore).findAllByIds(org.mockito.ArgumentMatchers.<List<AuthorId>>assertArg(ids ->
                assertThat(ids).containsExactlyInAnyOrder(firstAuthorId, secondAuthorId)));
    }

    @Test
    void test_getAuthorsForBooks_whenSomeAuthorIdsMissing_filtersUnknownAuthors() {
        BookId bookId = BookId.parse("8ac017c8-fafb-4f6c-88fe-59651bdc04b8");
        AuthorId knownAuthorId = AuthorId.parse("d50657f5-5e00-4117-ab97-e6a45e33e444");
        AuthorId missingAuthorId = AuthorId.parse("12c2d97c-4654-4398-bc0e-c40cf96715c6");
        ByIds<BookId> query = new ByIds<>(List.of(bookId));

        Author knownAuthor = Author.create(knownAuthorId, "George Orwell",
                TimeTestData.DEFAULT_CREATED_DATE, TimeTestData.DEFAULT_UPDATED_DATE, 1);

        when(bookAuthorLinkStore.findAuthorIdsByBookIds(query.ids())).thenReturn(Map.of(
                bookId, List.of(knownAuthorId, missingAuthorId)
        ));
        when(authorStore.findAllByIds(org.mockito.ArgumentMatchers.<List<AuthorId>>any()))
                .thenReturn(List.of(knownAuthor));

        Map<BookId, List<Author>> result = bookAuthorManagementService.getAuthorsForBooks(query);

        assertSoftly(softly -> {
            softly.assertThat(result).containsOnlyKeys(bookId);
            softly.assertThat(result.get(bookId)).containsExactly(knownAuthor);
        });
    }
}
