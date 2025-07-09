package org.bk.graphql.repository

import org.bk.graphql.entity.AuthorEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository : CrudRepository<AuthorEntity, String>, PagingAndSortingRepository<AuthorEntity, String>