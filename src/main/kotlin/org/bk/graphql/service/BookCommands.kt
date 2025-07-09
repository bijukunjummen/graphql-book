package org.bk.graphql.service

import org.bk.graphql.domain.AuthorId

data class CreateBookCommand(val name: String, val pageCount: Int, val authors: Set<AuthorId>)
data class CreateOrUpdateBookCommand(val id: String, val name: String, val pageCount: Int, val authors: Set<AuthorId>, val version: Int = 0)
data class UpdateBookCommand(val id: String, val name: String, val pageCount: Int, val authors: Set<AuthorId>, val version: Int)