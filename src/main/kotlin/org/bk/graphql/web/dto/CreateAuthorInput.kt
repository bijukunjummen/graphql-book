package org.bk.graphql.web.dto

data class CreateAuthorInput(
    val firstName: String,
    val lastName: String,
    val version: Int = 0
)
