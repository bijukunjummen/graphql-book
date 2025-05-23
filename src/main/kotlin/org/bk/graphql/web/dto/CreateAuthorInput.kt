package org.bk.graphql.web.dto

data class CreateAuthorInput(
    val name: String,
    val version: Int = 0
)
