CREATE TABLE book_rank (
    id UUID PRIMARY KEY,
    book_id UUID REFERENCES books(id) ON DELETE CASCADE,
    rank_value NUMERIC(38,18) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE INDEX idx_book_rank_order ON book_rank(rank_value, book_id);