package org.bk.graphql.service

data class CreateBookCommand(val name: String, val pageCount: Int, val authorId: String)
data class CreateOrUpdateBookCommand(val id: String, val name: String, val pageCount: Int, val authorId: String, val version: Int = 0)
data class UpdateBookCommand(val id: String, val name: String, val pageCount: Int, val authorId: String, val version: Int)