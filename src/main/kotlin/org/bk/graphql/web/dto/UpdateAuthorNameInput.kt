package org.bk.graphql.web.dto

data class UpdateAuthorNameInput(
    val id: String,
    val name: String,
    val version: Int
)
