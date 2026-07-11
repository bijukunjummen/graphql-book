package org.bk.graphql.service;

import org.bk.graphql.AuthorTestData;
import org.bk.graphql.TimeTestData;
import org.bk.graphql.application.port.out.AuthorStore;
import org.bk.graphql.common.query.ById;
import org.bk.graphql.common.query.ByIds;
import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.service.author.AuthorServiceImpl;
import org.bk.graphql.service.author.CreateAuthorCommand;
import org.bk.graphql.service.author.CreateOrUpdateAuthorCommand;
import org.bk.graphql.service.author.UpdateAuthorNameCommand;
import org.bk.graphql.service.exception.DomainException;
import org.bk.graphql.util.Uuids;
import org.springframework.context.ApplicationEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.bk.graphql.TimeTestData.DEFAULT_CREATED_DATE;
import static org.bk.graphql.TimeTestData.DEFAULT_UPDATED_DATE;
import static org.bk.graphql.TimeTestData.FIXED_CLOCK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {
    @InjectMocks
    private AuthorServiceImpl authorService;

    @Mock
    private AuthorStore authorStore;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Spy
    private Clock clock = TimeTestData.FIXED_CLOCK;

    @Spy
    private Uuids uuids = Uuids.fixedUuid(AuthorTestData.AUTHOR_ID_1.id());

    @Test
    void test_createAuthor_withValidCommand_returnsCreatedAuthorAndSavesEntity() {
        when(authorStore.save(any(Author.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Author createdAuthor = authorService.createAuthor(new CreateAuthorCommand("George Orwell"));

        assertSoftly(softly -> {
            softly.assertThat(createdAuthor.id()).isEqualTo(AuthorTestData.AUTHOR_ID_1);
            softly.assertThat(createdAuthor.name()).isEqualTo("George Orwell");
            softly.assertThat(createdAuthor.createdAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(createdAuthor.updatedAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(createdAuthor.version()).isZero();
        });
        verify(authorStore).save(assertArg(savedAuthor -> assertSoftly(softly -> {
            softly.assertThat(savedAuthor.id()).isEqualTo(AuthorTestData.AUTHOR_ID_1);
            softly.assertThat(savedAuthor.name()).isEqualTo("George Orwell");
            softly.assertThat(savedAuthor.createdAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedAuthor.updatedAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedAuthor.version()).isZero();
        })));
    }

    @Test
    void test_createOrUpdateAuthor_whenAuthorMissing_savesNewAuthorAndReturnsAuthor() {
        UUID authorId = UUID.fromString("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
        Author savedAuthor = author(authorId, "George Orwell", 0);
        when(authorStore.findById(AuthorId.of(authorId))).thenReturn(Optional.empty());
        when(authorStore.save(any(Author.class))).thenReturn(savedAuthor);

        Author author = authorService.createOrUpdateAuthor(new CreateOrUpdateAuthorCommand(authorId, "George Orwell"));

        assertAuthor(author, authorId, "George Orwell", 0);
        verify(authorStore).save(assertArg(savedAuthorEntity -> assertSoftly(softly -> {
            softly.assertThat(savedAuthorEntity.id()).isEqualTo(AuthorId.of(authorId));
            softly.assertThat(savedAuthorEntity.name()).isEqualTo("George Orwell");
            softly.assertThat(savedAuthorEntity.createdAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedAuthorEntity.updatedAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedAuthorEntity.version()).isZero();
        })));
    }

    @Test
    void test_createOrUpdateAuthor_whenAuthorExistsAndVersionPresent_savesUpdatedAuthorAndReturnsAuthor() {
        UUID authorId = UUID.fromString("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
        Author existingAuthor = author(authorId, "George Orwell", 1);
        Author savedAuthor = author(authorId, "George Orwell Updated", 2);
        when(authorStore.findById(AuthorId.of(authorId))).thenReturn(Optional.of(existingAuthor));
        when(authorStore.save(any(Author.class))).thenReturn(savedAuthor);

        Author author = authorService.createOrUpdateAuthor(new CreateOrUpdateAuthorCommand(authorId, "George Orwell Updated", 2));

        assertAuthor(author, authorId, "George Orwell Updated", 2);
        verify(authorStore).save(assertArg(savedAuthorEntity -> assertSoftly(softly -> {
            softly.assertThat(savedAuthorEntity.id()).isEqualTo(AuthorId.of(authorId));
            softly.assertThat(savedAuthorEntity.name()).isEqualTo("George Orwell Updated");
            softly.assertThat(savedAuthorEntity.createdAt()).isEqualTo(existingAuthor.createdAt());
            softly.assertThat(savedAuthorEntity.updatedAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedAuthorEntity.version()).isEqualTo(2);
        })));
    }

    @Test
    void test_createOrUpdateAuthor_whenAuthorExistsAndCommandVersionIsZero_doesNotSaveAndReturnsExistingAuthor() {
        UUID authorId = UUID.fromString("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
        Author existingAuthor = author(authorId, "George Orwell", 1);
        when(authorStore.findById(AuthorId.of(authorId))).thenReturn(Optional.of(existingAuthor));

        Author author = authorService.createOrUpdateAuthor(new CreateOrUpdateAuthorCommand(authorId, "Ignored"));

        assertAuthor(author, authorId, "George Orwell", 1);
        verify(authorStore, never()).save(any(Author.class));
    }

    @Test
    void test_updateAuthorName_whenAuthorExists_savesNameAndVersionOnly() {
        UUID authorId = UUID.fromString("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
        Author existingAuthor = author(authorId, "George Orwell", 1);
        Author savedAuthor = author(authorId, "Eric Blair", 2);
        when(authorStore.findById(AuthorId.of(authorId))).thenReturn(Optional.of(existingAuthor));
        when(authorStore.save(any(Author.class))).thenReturn(savedAuthor);

        Author author = authorService.updateAuthorName(new UpdateAuthorNameCommand(authorId, "Eric Blair", 2));

        assertAuthor(author, authorId, "Eric Blair", 2);
        verify(authorStore).save(assertArg(savedAuthorEntity -> assertSoftly(softly -> {
            softly.assertThat(savedAuthorEntity.id()).isEqualTo(AuthorId.of(authorId));
            softly.assertThat(savedAuthorEntity.name()).isEqualTo("Eric Blair");
            softly.assertThat(savedAuthorEntity.createdAt()).isEqualTo(existingAuthor.createdAt());
            softly.assertThat(savedAuthorEntity.updatedAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedAuthorEntity.version()).isEqualTo(2);
        })));
        verify(eventPublisher).publishEvent(org.mockito.ArgumentMatchers.any(Object.class));
    }

    @Test
    void test_updateAuthorName_whenAuthorMissing_throwsDomainException() {
        UUID authorId = UUID.fromString("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
        when(authorStore.findById(AuthorId.of(authorId))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.updateAuthorName(new UpdateAuthorNameCommand(authorId, "Eric Blair", 2)))
                .isInstanceOf(DomainException.class)
                .hasMessage("Author not found");

        verify(authorStore, never()).save(any(Author.class));
    }

    @Test
    void test_getAuthor_withExistingAuthorId_returnsAuthor() {
        UUID authorId = UUID.fromString("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
        when(authorStore.findById(AuthorId.of(authorId)))
                .thenReturn(Optional.of(author(authorId, "George Orwell", 1)));

        Author author = authorService.getAuthor(new ById<>(AuthorId.of(authorId)));

        assertAuthor(author, authorId, "George Orwell", 1);
    }

    @Test
    void test_getAuthors_withAuthorIds_returnsAuthors() {
        UUID firstAuthorId = UUID.fromString("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
        UUID secondAuthorId = UUID.fromString("e9f6a86e-4ffb-49d6-92ee-d1fe03cfa200");
        when(authorStore.findAllByIds(ArgumentMatchers.<List<AuthorId>>any()))
                .thenReturn(List.of(
                        author(firstAuthorId, "George Orwell", 1),
                        author(secondAuthorId, "Aldous Huxley", 1)
                ));

        List<Author> authors = authorService.getAuthors(new ByIds<>(List.of(AuthorId.of(firstAuthorId), AuthorId.of(secondAuthorId))));

        assertThat(authors)
                .hasSize(2)
                .satisfiesExactly(
                        author -> assertAuthor(author, firstAuthorId, "George Orwell", 1),
                        author -> assertAuthor(author, secondAuthorId, "Aldous Huxley", 1)
                );
        verify(authorStore).findAllByIds(ArgumentMatchers.<List<AuthorId>>assertArg(ids -> assertThat(ids)
                .containsExactlyInAnyOrder(AuthorId.of(firstAuthorId), AuthorId.of(secondAuthorId))));
    }

    @Test
    void test_getAuthors_withPageable_returnsMappedPage() {
        UUID authorId = UUID.fromString("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
        Pageable pageable = PageRequest.of(1, 3, Sort.by("name"));
        when(authorStore.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(author(authorId, "George Orwell", 1)), pageable, 1));

        Page<Author> page = authorService.getAuthors(pageable);

        assertThat(page.getContent())
                .singleElement()
                .satisfies(author -> assertAuthor(author, authorId, "George Orwell", 1));
        verify(authorStore).findAll(pageable);
    }

    private static Author author(UUID id, String name, int version) {
        return Author.create(AuthorId.of(id), name, DEFAULT_CREATED_DATE, DEFAULT_UPDATED_DATE, version);
    }

    private static void assertAuthor(Author author, UUID id, String name, int version) {
        assertSoftly(softly -> {
            softly.assertThat(author.id()).isEqualTo(AuthorId.of(id));
            softly.assertThat(author.name()).isEqualTo(name);
            softly.assertThat(author.version()).isEqualTo(version);
        });
    }
}
