package org.bk.books;

import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.author.ImmutableAuthor;

import java.util.UUID;

public final class AuthorTestData {
    public static final AuthorId AUTHOR_ID_1 = AuthorId.of(UUID.fromString("1259a393-f387-4900-a807-9519515c2b1e"));
    public static final String AUTHOR_NAME_1 = "John Doe";
    public static ImmutableAuthor sampleAuthor_1() {
        return ImmutableAuthor.builder()
                .id(AUTHOR_ID_1)
                .name(AUTHOR_NAME_1)
                .version(0)
                .createdAt(TimeTestData.DEFAULT_UPDATED_DATE)
                .updatedAt(TimeTestData.DEFAULT_UPDATED_DATE)
                .build();
    }
    private AuthorTestData() {}
}
