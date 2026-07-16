package org.bk.books.outbound.persistence;

import java.util.List;
import java.util.Optional;
import org.bk.books.domain.entity.book.Book;
import org.bk.books.domain.entity.book.BookId;
import org.bk.books.domain.entity.book.ImmutableBook;
import org.bk.books.entity.BookEntity;
import org.bk.books.port.BookStore;
import org.bk.books.repository.book.BookEntityEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class SpringDataBookStore implements BookStore {
    private final BookEntityEntityRepository bookEntityRepository;

    public SpringDataBookStore(BookEntityEntityRepository bookEntityRepository) {
        this.bookEntityRepository = bookEntityRepository;
    }

    @Override
    public Book save(Book book) {
        BookEntity saved = bookEntityRepository.save(BookEntity.fromModel(book));
        return ImmutableBook.builder()
                .from(saved.toModel())
                .authors(book.authors())
                .build();
    }

    @Override
    public Optional<Book> findById(BookId id) {
        return bookEntityRepository.findById(id.id()).map(BookEntity::toModel);
    }

    @Override
    public List<Book> findAllByIds(List<BookId> ids) {
        List<java.util.UUID> uuidIds = ids.stream().map(BookId::id).toList();
        return java.util.stream.StreamSupport.stream(
                        bookEntityRepository.findAllById(uuidIds).spliterator(), false)
                .map(BookEntity::toModel)
                .toList();
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        return bookEntityRepository.findAll(pageable).map(BookEntity::toModel);
    }
}
