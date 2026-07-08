package org.bk.graphql;

import org.bk.graphql.domain.Author;
import org.bk.graphql.domain.AuthorId;
import org.bk.graphql.domain.ImmutableAuthor;

import java.util.UUID;

public final class AuthorTestData {
    public static final AuthorId AUTHOR_ID_1 = new AuthorId(UUID.fromString("1259a393-f387-4900-a807-9519515c2b1e"));
    public static final String AUTHOR_NAME_1 = "John Doe";
    public static Author sampleAuthor() {
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
