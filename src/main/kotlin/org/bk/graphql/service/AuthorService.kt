package org.bk.graphql.service

import org.bk.graphql.domain.Author
import org.bk.graphql.domain.AuthorId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AuthorService {
    fun createAuthor(createAuthorCommand: CreateAuthorCommand): Author
    fun createOrUpdateAuthor(createOrUpdateAuthorCommand: CreateOrUpdateAuthorCommand): Author
    fun updateAuthor(command: UpdateAuthorCommand): Author
    fun updateAuthorName(command: UpdateAuthorNameCommand): Author
    fun getAuthors(query: GetAuthorsQuery): Page<Author>
    fun getAuthor(query: ById<AuthorId>): Author
    fun getAuthors(query: ByIds<AuthorId>): List<Author>
    fun getAuthors(pageable: Pageable): Page<Author>
}