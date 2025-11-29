package org.bk.graphql.entity;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("book_author")
public record AuthorRef(
    @Column("author_id") AggregateReference<AuthorEntity, String> author
) {
}

