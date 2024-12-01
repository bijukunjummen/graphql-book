package org.bk.graphql.service

data class GetAuthorsQuery(val page: Int, val size: Int)
data class GetAuthorQuery(val id: String)