CREATE TABLE BOOKS
(
    id         VARCHAR(255) PRIMARY KEY,
    name       VARCHAR(255),
    page_count int,
    author_id VARCHAR(255) references BOOKS(id),
    version    int
)
