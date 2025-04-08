package org.bk.graphql.web.dto

enum class OrderField {
    ASC, DESC
}

data class SortInput(val field: String, val order: OrderField = OrderField.ASC)