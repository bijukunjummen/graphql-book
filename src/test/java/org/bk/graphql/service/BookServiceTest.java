package org.bk.graphql.service;

import org.bk.graphql.entity.BookEntity;
import org.bk.graphql.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookRepository bookRepository;

    @Test
    void test_createBook_successful() {
        when(bookRepository.save(any(BookEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        bookService.createBook(new CreateBookCommand("book1", 100, Set.of()));
    }
}
