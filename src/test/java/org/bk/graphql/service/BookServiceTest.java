package org.bk.graphql.service;

import org.bk.graphql.TimeTestData;
import org.bk.graphql.common.query.ById;
import org.bk.graphql.common.query.ByIds;
import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.Book;
import org.bk.graphql.domain.BookId;
import org.bk.graphql.entity.BookEntity;
import org.bk.graphql.repository.book.BookRepository;
import org.bk.graphql.service.author.AuthorService;
import org.bk.graphql.service.book.BookCommands.CreateBookCommand;
import org.bk.graphql.service.book.BookCommands.CreateOrUpdateBookCommand;
import org.bk.graphql.service.book.BookCommands.UpdateBookCommand;
import org.bk.graphql.service.book.BookCommands.UpdateBookNameCommand;
import org.bk.graphql.service.book.BookQueries;
import org.bk.graphql.service.book.BookServiceImpl;
import org.bk.graphql.service.exception.DomainException;
import org.bk.graphql.util.Uuids;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.bk.graphql.BookTestData.BOOK_ID_1;
import static org.bk.graphql.TimeTestData.DEFAULT_CREATED_DATE;
import static org.bk.graphql.TimeTestData.DEFAULT_UPDATED_DATE;
import static org.bk.graphql.TimeTestData.FIXED_CLOCK;
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
    private BookRepository bookRepository;

    @Mock
    private AuthorService authorService;

    @Spy
    private Clock clock = TimeTestData.FIXED_CLOCK;

    @Spy
    private Uuids uuids = Uuids.fixedUuid(BOOK_ID);

    @Test
    void test_createBook_withValidCommand_returnsCreatedBookAndSavesEntity() {
        AuthorId authorId = AuthorId.parse("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
        when(bookRepository.save(any(BookEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Book createdBook = bookService.createBook(new CreateBookCommand("book1", 100, Set.of(authorId)));

        assertSoftly(softly -> {
            softly.assertThat(createdBook.id()).isEqualTo(BOOK_ID_1);
            softly.assertThat(createdBook.name()).isEqualTo("book1");
            softly.assertThat(createdBook.pageCount()).isEqualTo(100);
        });
        verify(bookRepository).save(assertArg(savedBook -> assertSoftly(softly -> {
            softly.assertThat(savedBook.id()).isEqualTo(BOOK_ID.toString());
            softly.assertThat(savedBook.name()).isEqualTo("book1");
            softly.assertThat(savedBook.pageCount()).isEqualTo(100);
            softly.assertThat(savedBook.createdAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedBook.updatedAt()).isEqualTo(FIXED_CLOCK.instant());
            softly.assertThat(savedBook.version()).isZero();
        })));
    }

    @Test
    void test_createOrUpdateBook_whenBookMissing_savesNewBookAndReturnsBook() {
        String bookId = "2f5ac49b-af88-4e72-a549-5f86aff4e549";
        AuthorId authorId = AuthorId.parse("c6aa1cb3-c9bd-47e0-ba1f-12a35027df8d");
        BookEntity savedBook = bookEntity(bookId, "Good Omens", 490, 0, authorId);
        when(bookRepository.findById(bookId))
                .thenReturn(Optional.empty());
        when(bookRepository.save(any(BookEntity.class))).thenReturn(savedBook);

        Book book = bookService.createOrUpdateBook(new CreateOrUpdateBookCommand(bookId, "Good Omens", 490, Set.of(authorId)));

        assertBook(book, bookId, "Good Omens", 490, 0, authorId);
        verify(bookRepository).save(assertArg(savedBookEntity -> assertSoftly(softly -> {
            softly.assertThat(savedBookEntity.id()).isEqualTo(bookId);
            softly.assertThat(savedBookEntity.name()).isEqualTo("Good Omens");
            softly.assertThat(savedBookEntity.pageCount()).isEqualTo(490);
            softly.assertThat(savedBookEntity.version()).isZero();
        })));
    }

    @Test
    void test_createOrUpdateBook_whenBookExistsAndVersionPresent_savesUpdatedBookAndReturnsBook() {
        String bookId = "2f5ac49b-af88-4e72-a549-5f86aff4e549";
        AuthorId authorId = AuthorId.parse("38469694-b350-4f1a-89be-1c8fd9aeaf2d");
        BookEntity existingBook = bookEntity(bookId, "Good Omens", 490, 1, AuthorId.parse("c6aa1cb3-c9bd-47e0-ba1f-12a35027df8d"));
        BookEntity savedBook = bookEntity(bookId, "Good Omens Updated", 512, 2, authorId);
        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(BookEntity.class))).thenReturn(savedBook);

        Book book = bookService.createOrUpdateBook(new CreateOrUpdateBookCommand(bookId, "Good Omens Updated", 512, Set.of(authorId), 2));

        assertBook(book, bookId, "Good Omens Updated", 512, 2, authorId);
        verify(bookRepository).save(assertArg(savedBookEntity -> assertSoftly(softly -> {
            softly.assertThat(savedBookEntity.id()).isEqualTo(bookId);
            softly.assertThat(savedBookEntity.name()).isEqualTo("Good Omens Updated");
            softly.assertThat(savedBookEntity.pageCount()).isEqualTo(512);
            softly.assertThat(savedBookEntity.version()).isEqualTo(2);
        })));
    }

    @Test
    void test_createOrUpdateBook_whenBookExistsAndCommandVersionIsZero_doesNotSaveAndReturnsExistingBook() {
        String bookId = "2f5ac49b-af88-4e72-a549-5f86aff4e549";
        AuthorId authorId = AuthorId.parse("c6aa1cb3-c9bd-47e0-ba1f-12a35027df8d");
        BookEntity existingBook = bookEntity(bookId, "Good Omens", 490, 1, authorId);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

        Book book = bookService.createOrUpdateBook(new CreateOrUpdateBookCommand(bookId, "Ignored", 111, Set.of(authorId)));

        assertBook(book, bookId, "Good Omens", 490, 1, authorId);
        verify(bookRepository, never()).save(any(BookEntity.class));
    }

    @Test
    void test_updateBook_whenBookExists_savesEditableFieldsAndReturnsBook() {
        String bookId = "d8d387ac-0b36-4d33-b6d2-9f1a4591ec35";
        AuthorId authorId = AuthorId.parse("f3b5bb7e-1f73-4ef0-bdc3-ef4f5e1d8c1e");
        BookEntity existingBook = bookEntity(bookId, "A Wizard of Earthsea", 205, 1, authorId);
        BookEntity savedBook = bookEntity(bookId, "The Tombs of Atuan", 180, 2, authorId);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(BookEntity.class))).thenReturn(savedBook);

        Book book = bookService.updateBook(new UpdateBookCommand(bookId, "The Tombs of Atuan", 180, Set.of(authorId), 2));

        assertBook(book, bookId, "The Tombs of Atuan", 180, 2, authorId);
        verify(bookRepository).save(assertArg(savedBookEntity -> assertSoftly(softly -> {
            softly.assertThat(savedBookEntity.id()).isEqualTo(bookId);
            softly.assertThat(savedBookEntity.name()).isEqualTo("The Tombs of Atuan");
            softly.assertThat(savedBookEntity.pageCount()).isEqualTo(180);
            softly.assertThat(savedBookEntity.version()).isEqualTo(2);
        })));
    }

    @Test
    void test_updateBook_whenBookMissing_throwsDomainException() {
        String bookId = "d8d387ac-0b36-4d33-b6d2-9f1a4591ec35";
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(new UpdateBookCommand(bookId, "The Tombs of Atuan", 180, Set.of(), 2)))
                .isInstanceOf(DomainException.class)
                .hasMessage("Book not found");

        verify(bookRepository, never()).save(any(BookEntity.class));
    }

    @Test
    void test_updateBookName_whenBookExists_savesNameAndVersionOnly() {
        String bookId = "d8d387ac-0b36-4d33-b6d2-9f1a4591ec35";
        AuthorId authorId = AuthorId.parse("f3b5bb7e-1f73-4ef0-bdc3-ef4f5e1d8c1e");
        BookEntity existingBook = bookEntity(bookId, "A Wizard of Earthsea", 205, 1, authorId);
        BookEntity savedBook = bookEntity(bookId, "The Tombs of Atuan", 205, 2, authorId);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(BookEntity.class))).thenReturn(savedBook);

        Book book = bookService.updateBookName(new UpdateBookNameCommand(bookId, "The Tombs of Atuan", 2));

        assertBook(book, bookId, "The Tombs of Atuan", 205, 2, authorId);
        verify(bookRepository).save(assertArg(savedBookEntity -> assertSoftly(softly -> {
            softly.assertThat(savedBookEntity.id()).isEqualTo(bookId);
            softly.assertThat(savedBookEntity.name()).isEqualTo("The Tombs of Atuan");
            softly.assertThat(savedBookEntity.pageCount()).isEqualTo(205);
            softly.assertThat(savedBookEntity.version()).isEqualTo(2);
        })));
    }

    @Test
    void test_updateBookName_whenBookMissing_throwsDomainException() {
        String bookId = "d8d387ac-0b36-4d33-b6d2-9f1a4591ec35";
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBookName(new UpdateBookNameCommand(bookId, "The Tombs of Atuan", 2)))
                .isInstanceOf(DomainException.class)
                .hasMessage("Book not found");

        verify(bookRepository, never()).save(any(BookEntity.class));
    }

    @Test
    void test_getBooks_withQuery_returnsMappedPageUsingRequestedPage() {
        String bookId = "8ac017c8-fafb-4f6c-88fe-59651bdc04b8";
        AuthorId authorId = AuthorId.parse("d50657f5-5e00-4117-ab97-e6a45e33e444");
        when(bookRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(bookEntity(bookId, "1984", 328, 1, authorId))));

        Page<Book> page = bookService.getBooks(new BookQueries.GetBooksQuery(2, 5));

        assertThat(page.getContent())
                .singleElement()
                .satisfies(book -> assertBook(book, bookId, "1984", 328, 1, authorId));
        verify(bookRepository).findAll(ArgumentMatchers.<Pageable>assertArg(pageable -> assertSoftly(softly -> {
            softly.assertThat(pageable.getPageNumber()).isEqualTo(2);
            softly.assertThat(pageable.getPageSize()).isEqualTo(5);
            softly.assertThat(pageable.getOffset()).isEqualTo(10);
        })));
    }

    @Test
    void test_getBooks_withPageable_returnsMappedPage() {
        String bookId = "8ac017c8-fafb-4f6c-88fe-59651bdc04b8";
        AuthorId authorId = AuthorId.parse("d50657f5-5e00-4117-ab97-e6a45e33e444");
        Pageable pageable = PageRequest.of(1, 3, Sort.by("name"));
        when(bookRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(bookEntity(bookId, "1984", 328, 1, authorId)), pageable, 1));

        Page<Book> page = bookService.getBooks(pageable);

        assertThat(page.getContent())
                .singleElement()
                .satisfies(book -> assertBook(book, bookId, "1984", 328, 1, authorId));
        verify(bookRepository).findAll(pageable);
    }

    @Test
    void test_getBook_withExistingBookId_returnsBook() {
        String bookId = "8ac017c8-fafb-4f6c-88fe-59651bdc04b8";
        AuthorId authorId = AuthorId.parse("d50657f5-5e00-4117-ab97-e6a45e33e444");
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity(bookId, "1984", 328, 1, authorId)));

        Optional<Book> book = bookService.getBook(new ById<>(BookId.parse(bookId)));

        assertThat(book)
                .hasValueSatisfying(foundBook -> assertBook(foundBook, bookId, "1984", 328, 1, authorId));
    }

    @Test
    void test_getBooks_withBookIds_returnsBooks() {
        String firstBookId = "8ac017c8-fafb-4f6c-88fe-59651bdc04b8";
        String secondBookId = "c22ee984-7f74-4158-8bd5-79235b0ad051";
        AuthorId firstAuthorId = AuthorId.parse("d50657f5-5e00-4117-ab97-e6a45e33e444");
        AuthorId secondAuthorId = AuthorId.parse("12c2d97c-4654-4398-bc0e-c40cf96715c6");
        when(bookRepository.findAllById(List.of(firstBookId, secondBookId)))
                .thenReturn(List.of(
                        bookEntity(firstBookId, "1984", 328, 1, firstAuthorId),
                        bookEntity(secondBookId, "Brave New World", 268, 1, secondAuthorId)
                ));

        List<Book> books = bookService.getBooks(new ByIds<>(List.of(BookId.parse(firstBookId), BookId.parse(secondBookId))));

        assertThat(books)
                .hasSize(2)
                .satisfiesExactly(
                        book -> assertBook(book, firstBookId, "1984", 328, 1, firstAuthorId),
                        book -> assertBook(book, secondBookId, "Brave New World", 268, 1, secondAuthorId)
                );
    }

    @Test
    void test_getAuthorsForBooks_withBookIds_returnsAuthorsByBook() {
        String firstBookId = "8ac017c8-fafb-4f6c-88fe-59651bdc04b8";
        String secondBookId = "c22ee984-7f74-4158-8bd5-79235b0ad051";
        AuthorId firstAuthorId = AuthorId.parse("d50657f5-5e00-4117-ab97-e6a45e33e444");
        AuthorId secondAuthorId = AuthorId.parse("12c2d97c-4654-4398-bc0e-c40cf96715c6");
        Author firstAuthor = Author.create(firstAuthorId, "George Orwell", DEFAULT_CREATED_DATE, DEFAULT_UPDATED_DATE, 1);
        Author secondAuthor = Author.create(secondAuthorId, "Aldous Huxley", DEFAULT_CREATED_DATE, DEFAULT_UPDATED_DATE, 1);
        when(bookRepository.findAllById(List.of(firstBookId, secondBookId)))
                .thenReturn(List.of(
                        bookEntity(firstBookId, "1984", 328, 1, firstAuthorId),
                        bookEntity(secondBookId, "Brave New World", 268, 1, secondAuthorId)
                ));
        when(authorService.getAuthors(ArgumentMatchers.<ByIds<AuthorId>>any()))
                .thenReturn(List.of(firstAuthor, secondAuthor));

        var authorsByBook = bookService.getAuthorsForBooks(new ByIds<>(List.of(BookId.parse(firstBookId), BookId.parse(secondBookId))));

        assertThat(authorsByBook)
                .containsEntry(BookId.parse(firstBookId), List.of(firstAuthor))
                .containsEntry(BookId.parse(secondBookId), List.of(secondAuthor));
        verify(authorService).getAuthors(ArgumentMatchers.<ByIds<AuthorId>>assertArg(query -> assertThat(query.ids())
                .containsExactly(firstAuthorId, secondAuthorId)));
    }

    private static BookEntity bookEntity(String id, String name, int pageCount, int version, AuthorId authorId) {
        return new BookEntity(id, name, pageCount, DEFAULT_CREATED_DATE, DEFAULT_UPDATED_DATE, version);
    }


    private static void assertBook(Book book, String id, String name, int pageCount, int version, AuthorId authorId) {
        assertSoftly(softly -> {
            softly.assertThat(book.id()).isEqualTo(BookId.parse(id));
            softly.assertThat(book.name()).isEqualTo(name);
            softly.assertThat(book.pageCount()).isEqualTo(pageCount);
            softly.assertThat(book.version()).isEqualTo(version);
        });
    }
}
