package org.bk.books;

import org.bk.books.domain.BookAuthorLink;
import org.bk.books.domain.BookAuthorLinkId;
import org.bk.books.domain.ImmutableBookAuthorLink;

import java.util.UUID;

public class BookAuthorLinkTestData {

    public static BookAuthorLinkId BOOK_AUTHOR_LINK_ID = BookAuthorLinkId.of(UUID.fromString("f7b894f3-9390-41b4-a96f-7cd5fcbeb02b"));

    public static BookAuthorLink sampleBookAuthorLink() {
        return ImmutableBookAuthorLink.builder()
                .id(BOOK_AUTHOR_LINK_ID)
                .authorId(AuthorTestData.AUTHOR_ID_1)
                .bookId(BookTestData.BOOK_ID_1)
                .createdAt(TimeTestData.DEFAULT_CREATED_DATE)
                .updatedAt(TimeTestData.DEFAULT_UPDATED_DATE)
                .build();
    }
    private BookAuthorLinkTestData() {}
}
