package org.bk.graphql.web.dto

data class SortInput(val field: String, val order: OrderField = OrderField.ASC)
