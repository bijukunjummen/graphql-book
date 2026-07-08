package org.bk.graphql.service;

import org.bk.graphql.TimeTestData;
import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.entity.AuthorEntity;
import org.bk.graphql.repository.AuthorRepository;
import org.bk.graphql.service.exception.DomainException;
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
import java.util.Set;

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
    private AuthorRepository authorRepository;

    @Spy
    private Clock clock = TimeTestData.FIXED_CLOCK;

    p

    @Test
    void test_createAuthor_withValidCommand_returnsCreatedAuthorAndSavesEntity() {
        when(authorRepository.save(any(AuthorEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Author createdAuthor = authorService.createAuthor(new CreateAuthorCommand("George Orwell"));

        assertSoftly(softly -> {
            softly.assertThat(createdAuthor.id()).isNotNull();
            softly.assertThat(createdAuthor.name()).isEqualTo("George Orwell");
            softly.assertThat(createdAuthor.createdAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(createdAuthor.updatedAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(createdAuthor.version()).isZero();
        });
        verify(authorRepository).save(assertArg(savedAuthor -> assertSoftly(softly -> {
            softly.assertThat(savedAuthor.id()).isNotBlank();
            softly.assertThat(savedAuthor.name()).isEqualTo("George Orwell");
            softly.assertThat(savedAuthor.createdAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedAuthor.updatedAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedAuthor.version()).isZero();
        })));
    }

    @Test
    void test_createOrUpdateAuthor_whenAuthorMissing_savesNewAuthorAndReturnsAuthor() {
        String authorId = "38469694-b350-4f1a-89be-1c8fd9aeaf2d";
        AuthorEntity savedAuthor = authorEntity(authorId, "George Orwell", 0);
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());
        when(authorRepository.save(any(AuthorEntity.class))).thenReturn(savedAuthor);

        Author author = authorService.createOrUpdateAuthor(new CreateOrUpdateAuthorCommand(authorId, "George Orwell"));

        assertAuthor(author, authorId, "George Orwell", 0);
        verify(authorRepository).save(assertArg(savedAuthorEntity -> assertSoftly(softly -> {
            softly.assertThat(savedAuthorEntity.id()).isEqualTo(authorId);
            softly.assertThat(savedAuthorEntity.name()).isEqualTo("George Orwell");
            softly.assertThat(savedAuthorEntity.createdAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedAuthorEntity.updatedAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedAuthorEntity.version()).isZero();
        })));
    }

    @Test
    void test_createOrUpdateAuthor_whenAuthorExistsAndVersionPresent_savesUpdatedAuthorAndReturnsAuthor() {
        String authorId = "38469694-b350-4f1a-89be-1c8fd9aeaf2d";
        AuthorEntity existingAuthor = authorEntity(authorId, "George Orwell", 1);
        AuthorEntity savedAuthor = authorEntity(authorId, "George Orwell Updated", 2);
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(AuthorEntity.class))).thenReturn(savedAuthor);

        Author author = authorService.createOrUpdateAuthor(new CreateOrUpdateAuthorCommand(authorId, "George Orwell Updated", 2));

        assertAuthor(author, authorId, "George Orwell Updated", 2);
        verify(authorRepository).save(assertArg(savedAuthorEntity -> assertSoftly(softly -> {
            softly.assertThat(savedAuthorEntity.id()).isEqualTo(authorId);
            softly.assertThat(savedAuthorEntity.name()).isEqualTo("George Orwell Updated");
            softly.assertThat(savedAuthorEntity.createdAt()).isEqualTo(existingAuthor.createdAt());
            softly.assertThat(savedAuthorEntity.updatedAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedAuthorEntity.version()).isEqualTo(2);
        })));
    }

    @Test
    void test_createOrUpdateAuthor_whenAuthorExistsAndCommandVersionIsZero_doesNotSaveAndReturnsExistingAuthor() {
        String authorId = "38469694-b350-4f1a-89be-1c8fd9aeaf2d";
        AuthorEntity existingAuthor = authorEntity(authorId, "George Orwell", 1);
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));

        Author author = authorService.createOrUpdateAuthor(new CreateOrUpdateAuthorCommand(authorId, "Ignored"));

        assertAuthor(author, authorId, "George Orwell", 1);
        verify(authorRepository, never()).save(any(AuthorEntity.class));
    }

    @Test
    void test_updateAuthorName_whenAuthorExists_savesNameAndVersionOnly() {
        String authorId = "38469694-b350-4f1a-89be-1c8fd9aeaf2d";
        AuthorEntity existingAuthor = authorEntity(authorId, "George Orwell", 1);
        AuthorEntity savedAuthor = authorEntity(authorId, "Eric Blair", 2);
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(AuthorEntity.class))).thenReturn(savedAuthor);

        Author author = authorService.updateAuthorName(new UpdateAuthorNameCommand(authorId, "Eric Blair", 2));

        assertAuthor(author, authorId, "Eric Blair", 2);
        verify(authorRepository).save(assertArg(savedAuthorEntity -> assertSoftly(softly -> {
            softly.assertThat(savedAuthorEntity.id()).isEqualTo(authorId);
            softly.assertThat(savedAuthorEntity.name()).isEqualTo("Eric Blair");
            softly.assertThat(savedAuthorEntity.createdAt()).isEqualTo(existingAuthor.createdAt());
            softly.assertThat(savedAuthorEntity.updatedAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedAuthorEntity.version()).isEqualTo(2);
        })));
    }

    @Test
    void test_updateAuthorName_whenAuthorMissing_throwsDomainException() {
        String authorId = "38469694-b350-4f1a-89be-1c8fd9aeaf2d";
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.updateAuthorName(new UpdateAuthorNameCommand(authorId, "Eric Blair", 2)))
                .isInstanceOf(DomainException.class)
                .hasMessage("Author not found");

        verify(authorRepository, never()).save(any(AuthorEntity.class));
    }

    @Test
    void test_getAuthor_withExistingAuthorId_returnsAuthor() {
        String authorId = "38469694-b350-4f1a-89be-1c8fd9aeaf2d";
        when(authorRepository.findById(AuthorId.parse(authorId).toString()))
                .thenReturn(Optional.of(authorEntity(authorId, "George Orwell", 1)));

        Author author = authorService.getAuthor(new ById<>(AuthorId.parse(authorId)));

        assertAuthor(author, authorId, "George Orwell", 1);
    }

    @Test
    void test_getAuthors_withAuthorIds_returnsAuthors() {
        String firstAuthorId = "38469694-b350-4f1a-89be-1c8fd9aeaf2d";
        String secondAuthorId = "e9f6a86e-4ffb-49d6-92ee-d1fe03cfa200";
        when(authorRepository.findAllByIdIn(ArgumentMatchers.<Set<String>>any()))
                .thenReturn(List.of(
                        authorEntity(firstAuthorId, "George Orwell", 1),
                        authorEntity(secondAuthorId, "Aldous Huxley", 1)
                ));

        List<Author> authors = authorService.getAuthors(new ByIds<>(List.of(AuthorId.parse(firstAuthorId), AuthorId.parse(secondAuthorId))));

        assertThat(authors)
                .hasSize(2)
                .satisfiesExactly(
                        author -> assertAuthor(author, firstAuthorId, "George Orwell", 1),
                        author -> assertAuthor(author, secondAuthorId, "Aldous Huxley", 1)
                );
        verify(authorRepository).findAllByIdIn(ArgumentMatchers.<Set<String>>assertArg(ids -> assertThat(ids)
                .containsExactlyInAnyOrder(firstAuthorId, secondAuthorId)));
    }

    @Test
    void test_getAuthors_withPageable_returnsMappedPage() {
        String authorId = "38469694-b350-4f1a-89be-1c8fd9aeaf2d";
        Pageable pageable = PageRequest.of(1, 3, Sort.by("name"));
        when(authorRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(authorEntity(authorId, "George Orwell", 1)), pageable, 1));

        Page<Author> page = authorService.getAuthors(pageable);

        assertThat(page.getContent())
                .singleElement()
                .satisfies(author -> assertAuthor(author, authorId, "George Orwell", 1));
        verify(authorRepository).findAll(pageable);
    }

    private static AuthorEntity authorEntity(String id, String name, int version) {
        return new AuthorEntity(id, name, DEFAULT_CREATED_DATE, DEFAULT_UPDATED_DATE, version);
    }

    private static void assertAuthor(Author author, String id, String name, int version) {
        assertSoftly(softly -> {
            softly.assertThat(author.id()).isEqualTo(AuthorId.parse(id));
            softly.assertThat(author.name()).isEqualTo(name);
            softly.assertThat(author.version()).isEqualTo(version);
        });
    }
}
