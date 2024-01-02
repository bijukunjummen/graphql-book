package org.bk.graphql

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GraphqlBookApplication

fun main(args: Array<String>) {
	runApplication<GraphqlBookApplication>(*args)
}
