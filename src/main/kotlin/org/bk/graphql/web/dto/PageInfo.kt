package org.bk.graphql.web.dto

data class PageInfo(
    val startCursor: String?,
    val endCursor: String?,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
)
