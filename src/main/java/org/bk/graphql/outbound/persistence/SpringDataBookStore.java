package org.bk.graphql.outbound.persistence;

import org.bk.graphql.application.port.out.BookStore;
import org.bk.graphql.domain.Book;
import org.bk.graphql.domain.BookId;
import org.bk.graphql.domain.ImmutableBook;
import org.bk.graphql.entity.BookEntity;
import org.bk.graphql.repository.book.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SpringDataBookStore implements BookStore {
    private final BookRepository bookRepository;

    public SpringDataBookStore(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        BookEntity saved = bookRepository.save(BookEntity.fromModel(book));
        return ImmutableBook.builder()
                .from(saved.toModel())
                .authors(book.authors())
                .build();
    }

    @Override
    public Optional<Book> findById(BookId id) {
        return bookRepository.findById(id.id()).map(BookEntity::toModel);
    }

    @Override
    public List<Book> findAllByIds(List<BookId> ids) {
        List<java.util.UUID> uuidIds = ids.stream().map(BookId::id).toList();
        return java.util.stream.StreamSupport.stream(bookRepository.findAllById(uuidIds).spliterator(), false)
                .map(BookEntity::toModel)
                .toList();
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(BookEntity::toModel);
    }
}
