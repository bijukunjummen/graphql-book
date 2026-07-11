package org.bk.books;

import org.bk.books.domain.Book;
import org.bk.books.domain.BookId;
import org.bk.books.domain.ImmutableBook;

import java.util.UUID;

public final class BookTestData {
    public static final BookId BOOK_ID_1 = BookId.of(UUID.fromString("2e2913b6-861c-442b-992f-b543e230095a"));

    public static final String BOOK_NAME_1 = "Book 1";

    public static Book sampleBook() {
        return ImmutableBook.builder()
                .id(BOOK_ID_1)
                .name(BOOK_NAME_1)
                .createdAt(TimeTestData.DEFAULT_CREATED_DATE)
                .updatedAt(TimeTestData.DEFAULT_UPDATED_DATE)
                .pageCount(100)
                .version(0)
                .build();
    }
    private BookTestData() {

    }
}
