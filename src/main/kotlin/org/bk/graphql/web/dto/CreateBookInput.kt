package org.bk.graphql.web.dto

data class CreateBookInput(val name: String, val pageCount: Int, val authors: Set<String>)