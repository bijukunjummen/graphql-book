package org.bk.books.service;

import org.bk.books.TimeTestData;
import org.bk.books.application.port.out.BookAuthorLinkStore;
import org.bk.books.application.port.out.BookStore;
import org.bk.books.common.query.ById;
import org.bk.books.common.query.ByIds;
import org.bk.books.domain.AuthorId;
import org.bk.books.domain.Book;
import org.bk.books.domain.BookId;
import org.bk.books.domain.event.BookAuthorsChangedEvent;
import org.bk.books.domain.event.BookCreatedEvent;
import org.bk.books.domain.ImmutableBook;
import org.bk.books.service.book.BookCommands.CreateBookCommand;
import org.bk.books.service.book.BookCommands.CreateOrUpdateBookCommand;
import org.bk.books.service.book.BookCommands.UpdateBookCommand;
import org.bk.books.service.book.BookCommands.UpdateBookNameCommand;
import org.bk.books.service.book.BookQueries;
import org.bk.books.service.book.BookServiceImpl;
import org.bk.books.service.exception.DomainException;
import org.bk.books.util.Uuids;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.bk.books.BookTestData.BOOK_ID_1;
import static org.bk.books.TimeTestData.DEFAULT_CREATED_DATE;
import static org.bk.books.TimeTestData.DEFAULT_UPDATED_DATE;
import static org.bk.books.TimeTestData.FIXED_CLOCK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    private static final UUID BOOK_ID = BOOK_ID_1.id();

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookStore bookStore;

    @Mock
    private BookAuthorLinkStore bookAuthorLinkStore;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Spy
    private Clock clock = TimeTestData.FIXED_CLOCK;

    @Spy
    private Uuids uuids = Uuids.fixedUuid(BOOK_ID);

    @Test
    void test_createBook_withValidCommand_returnsCreatedBookAndSavesEntity() {
        AuthorId authorId = AuthorId.parse("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
        when(bookStore.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Book createdBook = bookService.createBook(new CreateBookCommand("book1", 100, Set.of(authorId)));

        assertSoftly(softly -> {
            softly.assertThat(createdBook.id()).isEqualTo(BOOK_ID_1);
            softly.assertThat(createdBook.name()).isEqualTo("book1");
            softly.assertThat(createdBook.pageCount()).isEqualTo(100);
            softly.assertThat(createdBook.authors()).containsExactly(authorId);
        });
        verify(bookStore).save(assertArg(savedBook -> assertSoftly(softly -> {
            softly.assertThat(savedBook.id()).isEqualTo(BookId.of(BOOK_ID));
            softly.assertThat(savedBook.name()).isEqualTo("book1");
            softly.assertThat(savedBook.pageCount()).isEqualTo(100);
            softly.assertThat(savedBook.createdAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedBook.updatedAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedBook.version()).isZero();
        })));
        verify(bookAuthorLinkStore).replaceAuthorsForBook(BookId.of(BOOK_ID), Set.of(authorId), FIXED_CLOCK.instant());
        verify(eventPublisher).publishEvent(new BookCreatedEvent(BookId.of(BOOK_ID), List.of(authorId)));
    }

    @Test
    void test_createOrUpdateBook_whenBookMissing_savesNewBookAndReturnsBook() {
        UUID bookId = UUID.fromString("2f5ac49b-af88-4e72-a549-5f86aff4e549");
        AuthorId authorId = AuthorId.parse("c6aa1cb3-c9bd-47e0-ba1f-12a35027df8d");
        Book savedBook = book(bookId, "Good Omens", 490, 0, List.of());
        when(bookStore.findById(BookId.of(bookId)))
                .thenReturn(Optional.empty());
        when(bookStore.save(any(Book.class))).thenReturn(savedBook);

        Book book = bookService.createOrUpdateBook(new CreateOrUpdateBookCommand(bookId, "Good Omens", 490, Set.of(authorId)));

        assertBook(book, bookId, "Good Omens", 490, 0, authorId);
        verify(bookStore).save(assertArg(savedBookEntity -> assertSoftly(softly -> {
            softly.assertThat(savedBookEntity.id()).isEqualTo(BookId.of(bookId));
            softly.assertThat(savedBookEntity.name()).isEqualTo("Good Omens");
            softly.assertThat(savedBookEntity.pageCount()).isEqualTo(490);
            softly.assertThat(savedBookEntity.version()).isZero();
        })));
        verify(eventPublisher).publishEvent(new BookCreatedEvent(BookId.of(bookId), List.of(authorId)));
    }

    @Test
    void test_createOrUpdateBook_whenBookExistsAndVersionPresent_savesUpdatedBookAndReturnsBook() {
        UUID bookId = UUID.fromString("2f5ac49b-af88-4e72-a549-5f86aff4e549");
        AuthorId authorId = AuthorId.parse("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
        Book existingBook = book(bookId, "Good Omens", 490, 1, List.of());
        Book savedBook = book(bookId, "Good Omens Updated", 512, 2, List.of());
        when(bookStore.findById(BookId.of(bookId)))
                .thenReturn(Optional.of(existingBook));
        when(bookStore.save(any(Book.class))).thenReturn(savedBook);

        Book book = bookService.createOrUpdateBook(new CreateOrUpdateBookCommand(bookId, "Good Omens Updated", 512, Set.of(authorId), 2));

        assertBook(book, bookId, "Good Omens Updated", 512, 2, authorId);
        verify(bookStore).save(assertArg(savedBookEntity -> assertSoftly(softly -> {
            softly.assertThat(savedBookEntity.id()).isEqualTo(BookId.of(bookId));
            softly.assertThat(savedBookEntity.name()).isEqualTo("Good Omens Updated");
            softly.assertThat(savedBookEntity.pageCount()).isEqualTo(512);
            softly.assertThat(savedBookEntity.version()).isEqualTo(2);
        })));
        verify(bookAuthorLinkStore).replaceAuthorsForBook(BookId.of(bookId), Set.of(authorId), FIXED_CLOCK.instant());
        verify(eventPublisher).publishEvent(new BookAuthorsChangedEvent(BookId.of(bookId), List.of(authorId)));
    }

    @Test
    void test_createOrUpdateBook_whenBookExistsAndCommandVersionIsZero_doesNotSaveAndReturnsExistingBook() {
        UUID bookId = UUID.fromString("2f5ac49b-af88-4e72-a549-5f86aff4e549");
        AuthorId authorId = AuthorId.parse("c6aa1cb3-c9bd-47e0-ba1f-12a35027df8d");
        Book existingBook = book(bookId, "Good Omens", 490, 1, List.of(authorId));
        when(bookStore.findById(BookId.of(bookId))).thenReturn(Optional.of(existingBook));

        Book book = bookService.createOrUpdateBook(new CreateOrUpdateBookCommand(bookId, "Ignored", 111, Set.of(authorId)));

        assertBook(book, bookId, "Good Omens", 490, 1, authorId);
        verify(bookStore, never()).save(any(Book.class));
    }

    @Test
    void test_updateBook_whenBookExists_savesEditableFieldsAndReturnsBook() {
        UUID bookId = UUID.fromString("d8d387ac-0b36-4d33-b6d2-9f1a4591ec35");
        AuthorId authorId = AuthorId.parse("f3b5bb7e-1f73-4ef0-bdc3-ef4f5e1d8c1e");
        Book existingBook = book(bookId, "A Wizard of Earthsea", 205, 1, List.of());
        Book savedBook = book(bookId, "The Tombs of Atuan", 180, 2, List.of());
        when(bookStore.findById(BookId.of(bookId))).thenReturn(Optional.of(existingBook));
        when(bookStore.save(any(Book.class))).thenReturn(savedBook);

        Book book = bookService.updateBook(new UpdateBookCommand(bookId, "The Tombs of Atuan", 180, Set.of(authorId), 2));

        assertBook(book, bookId, "The Tombs of Atuan", 180, 2, authorId);
        verify(bookStore).save(assertArg(savedBookEntity -> assertSoftly(softly -> {
            softly.assertThat(savedBookEntity.id()).isEqualTo(BookId.of(bookId));
            softly.assertThat(savedBookEntity.name()).isEqualTo("The Tombs of Atuan");
            softly.assertThat(savedBookEntity.pageCount()).isEqualTo(180);
            softly.assertThat(savedBookEntity.version()).isEqualTo(2);
        })));
        verify(eventPublisher).publishEvent(new BookAuthorsChangedEvent(BookId.of(bookId), List.of(authorId)));
    }

    @Test
    void test_updateBook_whenBookMissing_throwsDomainException() {
        UUID bookId = UUID.fromString("d8d387ac-0b36-4d33-b6d2-9f1a4591ec35");
        when(bookStore.findById(BookId.of(bookId))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(new UpdateBookCommand(bookId, "The Tombs of Atuan", 180, Set.of(), 2)))
                .isInstanceOf(DomainException.class)
                .hasMessage("Book not found");

        verify(bookStore, never()).save(any(Book.class));
    }

    @Test
    void test_updateBookName_whenBookExists_savesNameAndVersionOnly() {
        UUID bookId = UUID.fromString("d8d387ac-0b36-4d33-b6d2-9f1a4591ec35");
        AuthorId authorId = AuthorId.parse("f3b5bb7e-1f73-4ef0-bdc3-ef4f5e1d8c1e");
        Book existingBook = book(bookId, "A Wizard of Earthsea", 205, 1, List.of());
        Book savedBook = book(bookId, "The Tombs of Atuan", 205, 2, List.of());
        when(bookStore.findById(BookId.of(bookId))).thenReturn(Optional.of(existingBook));
        when(bookStore.save(any(Book.class))).thenReturn(savedBook);
        when(bookAuthorLinkStore.findAuthorIdsByBookIds(List.of(BookId.of(bookId)))).thenReturn(Map.of(
                BookId.of(bookId), List.of(authorId)
        ));

        Book book = bookService.updateBookName(new UpdateBookNameCommand(bookId, "The Tombs of Atuan", 2));

        assertBook(book, bookId, "The Tombs of Atuan", 205, 2, authorId);
        verify(bookStore).save(assertArg(savedBookEntity -> assertSoftly(softly -> {
            softly.assertThat(savedBookEntity.id()).isEqualTo(BookId.of(bookId));
            softly.assertThat(savedBookEntity.name()).isEqualTo("The Tombs of Atuan");
            softly.assertThat(savedBookEntity.pageCount()).isEqualTo(205);
            softly.assertThat(savedBookEntity.version()).isEqualTo(2);
        })));
    }

    @Test
    void test_updateBookName_whenBookMissing_throwsDomainException() {
        UUID bookId = UUID.fromString("d8d387ac-0b36-4d33-b6d2-9f1a4591ec35");
        when(bookStore.findById(BookId.of(bookId))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBookName(new UpdateBookNameCommand(bookId, "The Tombs of Atuan", 2)))
                .isInstanceOf(DomainException.class)
                .hasMessage("Book not found");

        verify(bookStore, never()).save(any(Book.class));
    }

    @Test
    void test_getBooks_withQuery_returnsMappedPageUsingRequestedPage() {
        UUID bookId = UUID.fromString("8ac017c8-fafb-4f6c-88fe-59651bdc04b8");
        AuthorId authorId = AuthorId.parse("d50657f5-5e00-4117-ab97-e6a45e33e444");
        when(bookStore.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(book(bookId, "1984", 328, 1, List.of()))));
        when(bookAuthorLinkStore.findAuthorIdsByBookIds(List.of(BookId.of(bookId)))).thenReturn(Map.of(
                BookId.of(bookId), List.of(authorId)
        ));

        Page<Book> page = bookService.getBooks(new BookQueries.GetBooksQuery(2, 5));

        assertThat(page.getContent())
                .singleElement()
                .satisfies(book -> assertBook(book, bookId, "1984", 328, 1, authorId));
        verify(bookStore).findAll(ArgumentMatchers.<Pageable>assertArg(pageable -> assertSoftly(softly -> {
            softly.assertThat(pageable.getPageNumber()).isEqualTo(2);
            softly.assertThat(pageable.getPageSize()).isEqualTo(5);
            softly.assertThat(pageable.getOffset()).isEqualTo(10);
        })));
    }

    @Test
    void test_getBooks_withPageable_returnsMappedPage() {
        UUID bookId = UUID.fromString("8ac017c8-fafb-4f6c-88fe-59651bdc04b8");
        AuthorId authorId = AuthorId.parse("d50657f5-5e00-4117-ab97-e6a45e33e444");
        Pageable pageable = PageRequest.of(1, 3, Sort.by("name"));
        when(bookStore.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(book(bookId, "1984", 328, 1, List.of())), pageable, 1));
        when(bookAuthorLinkStore.findAuthorIdsByBookIds(List.of(BookId.of(bookId)))).thenReturn(Map.of(
                BookId.of(bookId), List.of(authorId)
        ));

        Page<Book> page = bookService.getBooks(pageable);

        assertThat(page.getContent())
                .singleElement()
                .satisfies(book -> assertBook(book, bookId, "1984", 328, 1, authorId));
        verify(bookStore).findAll(pageable);
    }

    @Test
    void test_getBook_withExistingBookId_returnsBook() {
        UUID bookId = UUID.fromString("8ac017c8-fafb-4f6c-88fe-59651bdc04b8");
        AuthorId authorId = AuthorId.parse("d50657f5-5e00-4117-ab97-e6a45e33e444");
        when(bookStore.findById(BookId.of(bookId))).thenReturn(Optional.of(book(bookId, "1984", 328, 1, List.of())));
        when(bookAuthorLinkStore.findAuthorIdsByBookIds(List.of(BookId.of(bookId)))).thenReturn(Map.of(
                BookId.of(bookId), List.of(authorId)
        ));

        Optional<Book> book = bookService.getBook(new ById<>(BookId.of(bookId)));

        assertThat(book)
                .hasValueSatisfying(foundBook -> assertBook(foundBook, bookId, "1984", 328, 1, authorId));
    }

    @Test
    void test_getBooks_withBookIds_returnsBooks() {
        UUID firstBookId = UUID.fromString("8ac017c8-fafb-4f6c-88fe-59651bdc04b8");
        UUID secondBookId = UUID.fromString("c22ee984-7f74-4158-8bd5-79235b0ad051");
        AuthorId firstAuthorId = AuthorId.parse("d50657f5-5e00-4117-ab97-e6a45e33e444");
        AuthorId secondAuthorId = AuthorId.parse("12c2d97c-4654-4398-bc0e-c40cf96715c6");
        when(bookStore.findAllByIds(List.of(BookId.of(firstBookId), BookId.of(secondBookId))))
                .thenReturn(List.of(
                        book(firstBookId, "1984", 328, 1, List.of()),
                        book(secondBookId, "Brave New World", 268, 1, List.of())
                ));
        when(bookAuthorLinkStore.findAuthorIdsByBookIds(List.of(BookId.of(firstBookId)))).thenReturn(Map.of(
                BookId.of(firstBookId), List.of(firstAuthorId)
        ));
        when(bookAuthorLinkStore.findAuthorIdsByBookIds(List.of(BookId.of(secondBookId)))).thenReturn(Map.of(
                BookId.of(secondBookId), List.of(secondAuthorId)
        ));

        List<Book> books = bookService.getBooks(new ByIds<>(List.of(BookId.of(firstBookId), BookId.of(secondBookId))));

        assertThat(books)
                .hasSize(2)
                .satisfiesExactly(
                        book -> assertBook(book, firstBookId, "1984", 328, 1, firstAuthorId),
                        book -> assertBook(book, secondBookId, "Brave New World", 268, 1, secondAuthorId)
                );
    }

    private static Book book(UUID id, String name, int pageCount, int version, List<AuthorId> authors) {
        return ImmutableBook.builder()
                .id(BookId.of(id))
                .name(name)
                .pageCount(pageCount)
                .authors(authors)
                .createdAt(DEFAULT_CREATED_DATE)
                .updatedAt(DEFAULT_UPDATED_DATE)
                .version(version)
                .build();
    }

    private static void assertBook(Book book, UUID id, String name, int pageCount, int version, AuthorId authorId) {
        assertSoftly(softly -> {
            softly.assertThat(book.id()).isEqualTo(BookId.of(id));
            softly.assertThat(book.name()).isEqualTo(name);
            softly.assertThat(book.pageCount()).isEqualTo(pageCount);
            softly.assertThat(book.version()).isEqualTo(version);
        });
    }
}
