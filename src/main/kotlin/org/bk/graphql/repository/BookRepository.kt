package org.bk.graphql.repository

import org.bk.graphql.entity.BookEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository: CrudRepository<BookEntity, String>, PagingAndSortingRepository<BookEntity, String>