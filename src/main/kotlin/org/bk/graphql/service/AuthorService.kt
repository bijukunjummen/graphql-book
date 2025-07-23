package org.bk.graphql.service

import org.bk.graphql.domain.Author
import org.bk.graphql.domain.AuthorId
import org.springframework.data.domain.Page

interface AuthorService {
    fun createAuthor(createAuthorCommand: CreateAuthorCommand): Author
    fun createOrUpdateAuthor(createOrUpdateAuthorCommand: CreateOrUpdateAuthorCommand): Author
    fun updateAuthor(updateAuthorCommand: UpdateAuthorCommand): Author
    fun getAuthors(getAuthorsQuery: GetAuthorsQuery): Page<Author>
    fun getAuthor(getAuthorQuery: ById<AuthorId>): Author
    fun getAuthors(ids: ByIds<AuthorId>): List<Author>
}