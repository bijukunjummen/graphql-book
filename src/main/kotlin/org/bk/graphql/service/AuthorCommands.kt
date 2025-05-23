package org.bk.graphql.service

data class CreateAuthorCommand(val name: String)
data class CreateOrUpdateAuthorCommand(val id: String, val name: String, val version: Int = 0)
data class UpdateAuthorCommand(val id: String, val name: String, val version: Int)