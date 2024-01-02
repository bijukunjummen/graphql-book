package org.bk.graphql.repository

import org.bk.graphql.model.Book
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository: CrudRepository<Book, String>, PagingAndSortingRepository<Book, String>