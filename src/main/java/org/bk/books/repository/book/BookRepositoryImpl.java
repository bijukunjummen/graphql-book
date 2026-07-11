package org.bk.books.repository.book;

import org.bk.books.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;

public class BookRepositoryImpl implements BookCustomRepository {
    private static final String QUERY = """
            SELECT b.* FROM
            books b LEFT JOIN book_rank br on br.book_id = b.id 
            ORDER BY
                CASE WHEN br.book_id IS NULL THEN 1 ELSE 0 END ASC,
                br.rank_value ASC NULLS LAST,
                b.name ASC,
                b.id ASC
            LIMIT :limit OFFSET :offset
            """;

    private static final String COUNT_QUERY = """
               select count(1) from books b
            """;
    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";
    private final JdbcClient jdbcClient;

    public BookRepositoryImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Page<BookEntity> getRankedBooks(Pageable pageable) {
        int limit = pageable.getPageSize();
        long offset = pageable.getOffset();
        final List<BookEntity> content = jdbcClient.sql(QUERY)
                .param(LIMIT, limit)
                .param(OFFSET, offset)
                .query(BookEntity.class)
                .list();
        return PageableExecutionUtils.getPage(content, pageable, () -> jdbcClient.sql(COUNT_QUERY).query(Long.class).single());
    }
}
