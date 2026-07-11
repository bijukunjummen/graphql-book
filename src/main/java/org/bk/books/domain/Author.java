package org.bk.books.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bk.books.domain.validation.AuthorName;
import org.immutables.value.Value;

import java.time.Instant;

@Value.Immutable
@JsonSerialize(as = ImmutableAuthor.class)
@JsonDeserialize(as = ImmutableAuthor.class)
public interface Author {
    static Author create(AuthorId authorId, String name, Instant createdAt, Instant updatedAt, int version) {
        return ImmutableAuthor.builder()
                .id(authorId)
                .name(AuthorName.of(name).value())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .version(version)
                .build();
    }

    AuthorId id();
    String name();
    Instant createdAt();
    Instant updatedAt();
    int version();
}