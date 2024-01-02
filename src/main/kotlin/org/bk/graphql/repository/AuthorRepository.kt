package org.bk.graphql.repository

import org.bk.graphql.model.Author
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository : CrudRepository<Author, String>, PagingAndSortingRepository<Author, String>