package org.bk.graphql.service

data class CreateAuthorCommand(val firstName: String, val lastName: String, val age: Int = 0)
data class CreateOrUpdateAuthorCommand(val id: String, val firstName: String, val lastName: String, val age: Int = 0, val version: Int = 0)
data class UpdateAuthorCommand(val id: String, val firstName: String, val lastName: String, val age: Int, val version: Int)