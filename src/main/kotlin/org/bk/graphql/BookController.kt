package org.bk.graphql

import org.bk.graphql.model.Author
import org.bk.graphql.model.Book
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class BookController {

    @QueryMapping
    fun findBookById(@Argument id: String): Book {
        return Book(id, "name: $id", 100, id)
    }

    @SchemaMapping(typeName = "Book", field = "author")
    fun author(book: Book): Author {
        return Author(book.authorId, "first", "last")
    }
}