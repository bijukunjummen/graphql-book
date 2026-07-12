package org.bk.books.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.bk.books.TimeTestData.DEFAULT_CREATED_DATE;
import static org.bk.books.TimeTestData.DEFAULT_UPDATED_DATE;
import static org.bk.books.TimeTestData.FIXED_CLOCK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import org.bk.books.AuthorTestData;
import org.bk.books.TimeTestData;
import org.bk.books.application.port.out.AuthorStore;
import org.bk.books.common.query.ById;
import org.bk.books.common.query.ByIds;
import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.service.author.AuthorServiceCommands.CreateAuthorCommand;
import org.bk.books.service.author.AuthorServiceCommands.CreateOrUpdateAuthorCommand;
import org.bk.books.service.author.AuthorServiceCommands.UpdateAuthorNameCommand;
import org.bk.books.service.author.AuthorServiceImpl;
import org.bk.books.service.exception.DomainException;
import org.bk.books.util.Uuids;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {
  @InjectMocks private AuthorServiceImpl authorService;

  @Mock private AuthorStore authorStore;

  @Mock private ApplicationEventPublisher eventPublisher;

  @Spy private Clock clock = TimeTestData.FIXED_CLOCK;

  @Spy private Uuids uuids = Uuids.fixedUuid(AuthorTestData.AUTHOR_ID_1.id());

  @Test
  void test_createAuthor_withValidCommand_returnsCreatedAuthorAndSavesEntity() {
    when(authorStore.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Author createdAuthor = authorService.createAuthor(new CreateAuthorCommand("George Orwell"));

    assertSoftly(
        softly -> {
          softly.assertThat(createdAuthor.id()).isEqualTo(AuthorTestData.AUTHOR_ID_1);
          softly.assertThat(createdAuthor.name()).isEqualTo("George Orwell");
          softly.assertThat(createdAuthor.createdAt()).isEqualTo(FIXED_CLOCK.instant());
          softly.assertThat(createdAuthor.updatedAt()).isEqualTo(FIXED_CLOCK.instant());
          softly.assertThat(createdAuthor.version()).isZero();
        });
    verify(authorStore)
        .save(
            assertArg(
                savedAuthor ->
                    assertSoftly(
                        softly -> {
                          softly.assertThat(savedAuthor.id()).isEqualTo(AuthorTestData.AUTHOR_ID_1);
                          softly.assertThat(savedAuthor.name()).isEqualTo("George Orwell");
                          softly
                              .assertThat(savedAuthor.createdAt())
                              .isEqualTo(FIXED_CLOCK.instant());
                          softly
                              .assertThat(savedAuthor.updatedAt())
                              .isEqualTo(FIXED_CLOCK.instant());
                          softly.assertThat(savedAuthor.version()).isZero();
                        })));
  }

  @Test
  void test_createOrUpdateAuthor_whenAuthorMissing_savesNewAuthorAndReturnsAuthor() {
    AuthorId authorId = AuthorId.parse("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
    Author savedAuthor = author(authorId, "George Orwell", 0);
    when(authorStore.findById(authorId)).thenReturn(Optional.empty());
    when(authorStore.save(any(Author.class))).thenReturn(savedAuthor);

    Author author =
        authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(authorId, "George Orwell"));

    assertAuthor(author, authorId, "George Orwell", 0);
    verify(authorStore)
        .save(
            assertArg(
                savedAuthorEntity ->
                    assertSoftly(
                        softly -> {
                          softly.assertThat(savedAuthorEntity.id()).isEqualTo(authorId);
                          softly.assertThat(savedAuthorEntity.name()).isEqualTo("George Orwell");
                          softly
                              .assertThat(savedAuthorEntity.createdAt())
                              .isEqualTo(FIXED_CLOCK.instant());
                          softly
                              .assertThat(savedAuthorEntity.updatedAt())
                              .isEqualTo(FIXED_CLOCK.instant());
                          softly.assertThat(savedAuthorEntity.version()).isZero();
                        })));
  }

  @Test
  void
      test_createOrUpdateAuthor_whenAuthorExistsAndVersionPresent_savesUpdatedAuthorAndReturnsAuthor() {
    AuthorId authorId = AuthorId.parse("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
    Author existingAuthor = author(authorId, "George Orwell", 1);
    Author savedAuthor = author(authorId, "George Orwell Updated", 2);
    when(authorStore.findById(authorId)).thenReturn(Optional.of(existingAuthor));
    when(authorStore.save(any(Author.class))).thenReturn(savedAuthor);

    Author author =
        authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(authorId, "George Orwell Updated", 2));

    assertAuthor(author, authorId, "George Orwell Updated", 2);
    verify(authorStore)
        .save(
            assertArg(
                savedAuthorEntity ->
                    assertSoftly(
                        softly -> {
                          softly.assertThat(savedAuthorEntity.id()).isEqualTo(authorId);
                          softly
                              .assertThat(savedAuthorEntity.name())
                              .isEqualTo("George Orwell Updated");
                          softly
                              .assertThat(savedAuthorEntity.createdAt())
                              .isEqualTo(existingAuthor.createdAt());
                          softly
                              .assertThat(savedAuthorEntity.updatedAt())
                              .isEqualTo(FIXED_CLOCK.instant());
                          softly.assertThat(savedAuthorEntity.version()).isEqualTo(2);
                        })));
  }

  @Test
  void
      test_createOrUpdateAuthor_whenAuthorExistsAndCommandVersionIsZero_doesNotSaveAndReturnsExistingAuthor() {
    AuthorId authorId = AuthorId.parse("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
    Author existingAuthor = author(authorId, "George Orwell", 1);
    when(authorStore.findById(authorId)).thenReturn(Optional.of(existingAuthor));

    Author author =
        authorService.createOrUpdateAuthor(new CreateOrUpdateAuthorCommand(authorId, "Ignored"));

    assertAuthor(author, authorId, "George Orwell", 1);
    verify(authorStore, never()).save(any(Author.class));
  }

  @Test
  void test_updateAuthorName_whenAuthorExists_savesNameAndVersionOnly() {
    AuthorId authorId = AuthorId.parse("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
    Author existingAuthor = author(authorId, "George Orwell", 1);
    Author savedAuthor = author(authorId, "Eric Blair", 2);
    when(authorStore.findById(authorId)).thenReturn(Optional.of(existingAuthor));
    when(authorStore.save(any(Author.class))).thenReturn(savedAuthor);

    Author author =
        authorService.updateAuthorName(new UpdateAuthorNameCommand(authorId, "Eric Blair", 2));

    assertAuthor(author, authorId, "Eric Blair", 2);
    verify(authorStore)
        .save(
            assertArg(
                savedAuthorEntity ->
                    assertSoftly(
                        softly -> {
                          softly.assertThat(savedAuthorEntity.id()).isEqualTo(authorId);
                          softly.assertThat(savedAuthorEntity.name()).isEqualTo("Eric Blair");
                          softly
                              .assertThat(savedAuthorEntity.createdAt())
                              .isEqualTo(existingAuthor.createdAt());
                          softly
                              .assertThat(savedAuthorEntity.updatedAt())
                              .isEqualTo(FIXED_CLOCK.instant());
                          softly.assertThat(savedAuthorEntity.version()).isEqualTo(2);
                        })));
    verify(eventPublisher).publishEvent(org.mockito.ArgumentMatchers.any(Object.class));
  }

  @Test
  void test_updateAuthorName_whenAuthorMissing_throwsDomainException() {
    AuthorId authorId = AuthorId.parse("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
    when(authorStore.findById(authorId)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                authorService.updateAuthorName(
                    new UpdateAuthorNameCommand(authorId, "Eric Blair", 2)))
        .isInstanceOf(DomainException.class)
        .hasMessage("Author not found");

    verify(authorStore, never()).save(any(Author.class));
  }

  @Test
  void test_getAuthor_withExistingAuthorId_returnsAuthor() {
    AuthorId authorId = AuthorId.parse("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
    when(authorStore.findById(authorId))
        .thenReturn(Optional.of(author(authorId, "George Orwell", 1)));

    Author author = authorService.getAuthor(new ById<>(authorId));

    assertAuthor(author, authorId, "George Orwell", 1);
  }

  @Test
  void test_getAuthors_withAuthorIds_returnsAuthors() {
    AuthorId firstAuthorId = AuthorId.parse("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
    AuthorId secondAuthorId = AuthorId.parse("e9f6a86e-4ffb-49d6-92ee-d1fe03cfa200");
    when(authorStore.findAllByIds(ArgumentMatchers.<List<AuthorId>>any()))
        .thenReturn(
            List.of(
                author(firstAuthorId, "George Orwell", 1),
                author(secondAuthorId, "Aldous Huxley", 1)));

    List<Author> authors =
        authorService.getAuthors(new ByIds<>(List.of(firstAuthorId, secondAuthorId)));

    assertThat(authors)
        .hasSize(2)
        .satisfiesExactly(
            author -> assertAuthor(author, firstAuthorId, "George Orwell", 1),
            author -> assertAuthor(author, secondAuthorId, "Aldous Huxley", 1));
    verify(authorStore)
        .findAllByIds(
            ArgumentMatchers.<List<AuthorId>>assertArg(
                ids -> assertThat(ids).containsExactlyInAnyOrder(firstAuthorId, secondAuthorId)));
  }

  @Test
  void test_getAuthors_withPageable_returnsMappedPage() {
    AuthorId authorId = AuthorId.parse("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
    Pageable pageable = PageRequest.of(1, 3, Sort.by("name"));
    when(authorStore.findAll(pageable))
        .thenReturn(new PageImpl<>(List.of(author(authorId, "George Orwell", 1)), pageable, 1));

    Page<Author> page = authorService.getAuthors(pageable);

    assertThat(page.getContent())
        .singleElement()
        .satisfies(author -> assertAuthor(author, authorId, "George Orwell", 1));
    verify(authorStore).findAll(pageable);
  }

  private static Author author(AuthorId id, String name, int version) {
    return Author.create(id, name, DEFAULT_CREATED_DATE, DEFAULT_UPDATED_DATE, version);
  }

  private static void assertAuthor(Author author, AuthorId id, String name, int version) {
    assertSoftly(
        softly -> {
          softly.assertThat(author.id()).isEqualTo(id);
          softly.assertThat(author.name()).isEqualTo(name);
          softly.assertThat(author.version()).isEqualTo(version);
        });
  }
}
