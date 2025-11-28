package org.bk.graphql

import org.bk.graphql.service.AuthorService
import org.bk.graphql.service.BookService
import org.bk.graphql.service.CreateOrUpdateAuthorCommand
import org.bk.graphql.service.CreateOrUpdateBookCommand
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service

@SpringBootApplication
class GraphqlBookApplication

fun main(args: Array<String>) {
    runApplication<GraphqlBookApplication>(*args)
}

@Service
class LoadDb(val bookService: BookService, val authorService: AuthorService) : ApplicationRunner {
    override fun run(args: ApplicationArguments): Unit {
        val a1 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "37fac9c2-d25d-45b6-945e-2cfe2a5b835b",
                name = "Frank Herbert"
            )
        )
        bookService.createOrUpdateBook(
            CreateOrUpdateBookCommand(
                id = "3d8b47d0-52df-480c-9fa2-b6a226d5220b",
                name = "Dune",
                pageCount = 200,
                authors = setOf(a1.id)
            )
        )

        val a2 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "728d11c9-b72b-46fb-b4a1-0557c3bf26a7",
                name = "Orson Scott",
            )
        )
        bookService.createOrUpdateBook(
            CreateOrUpdateBookCommand(
                id = "8028d1eb-0aa3-46fb-aecd-699a5e96e007",
                name = "Ender’s Game",
                pageCount = 200,
                authors = setOf(a2.id)
            )
        )

        val a3 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "18bcf1cb-d0c9-4a90-a53c-ba9041965672",
                name = "Douglas Adams"
            )
        )
        bookService.createOrUpdateBook(
            CreateOrUpdateBookCommand(
                id = "7f33a782-b811-4da0-8a18-e997aac2ea05",
                name = "The Hitchhiker's Guide To The Galaxy",
                pageCount = 200,
                authors = setOf(a3.id)
            )
        )

        val a4 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "f47621b4-b1c3-4c06-acbc-65ed8e6f9c76",
                name = "Mary Shelley"
            )
        )
        bookService.createOrUpdateBook(
            CreateOrUpdateBookCommand(
                id = "5f95e080-87b2-4234-87e7-95bf774099aa",
                name = "Frankenstein",
                pageCount = 200,
                authors = setOf(a4.id)
            )
        )

        val a5 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "d4c96974-eec5-4221-a24a-e0996c88d9b2",
                name = "Ray Bradbury"
            )
        )
        bookService.createOrUpdateBook(
            CreateOrUpdateBookCommand(
                id = "08dd1eda-3e94-46c5-8eab-b43e755d5545",
                name = "Fahrenheit 451",
                pageCount = 200,
                authors = setOf(a5.id)
            )
        )

        val a6 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "d50657f5-5e00-4117-ab97-e6a45e33e444",
                name = "George Orwell"
            )
        )
        bookService.createOrUpdateBook(
            CreateOrUpdateBookCommand(
                id = "8ac017c8-fafb-4f6c-88fe-59651bdc04b8",
                name = "1984",
                pageCount = 200,
                authors = setOf(a6.id)
            )
        )

        val a7 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "9b8904dd-c662-4a25-8a79-27bb29cac2a2",
                name = "Dan Simmons"
            )
        )
        bookService.createOrUpdateBook(
            CreateOrUpdateBookCommand(
                id = "6c513fd3-c95a-4b15-ac9f-640be1e60de0",
                name = "Hyperion",
                pageCount = 200,
                authors = setOf(a7.id)
            )
        )

        val a8 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "59bc76a3-feb7-42c1-99ee-41174348e005",
                name = "Philip Dick"
            )
        )
        bookService.createOrUpdateBook(
            CreateOrUpdateBookCommand(
                id = "0418ca6a-6c1f-4faa-b793-48e41bd93cfc",
                name = "Do Androids Dream of Electric Sheep",
                pageCount = 200,
                authors = setOf(a8.id)
            )
        )

        val a9 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "a3d7f148-0058-4fb5-8a85-a0e7e0a04130",
                name = "Isaac Asimov"
            )
        )
        bookService.createOrUpdateBook(
            CreateOrUpdateBookCommand(
                id = "a031c746-6a01-43d6-aa20-b35672d47255",
                name = "Foundation",
                pageCount = 200,
                authors = setOf(element = a9.id)
            )
        )

        val a10 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "075a9f48-6128-42dd-ab13-c1f45a2052d1",
                name = "H.G. Wells"
            )
        )
        bookService.createOrUpdateBook(
            CreateOrUpdateBookCommand(
                id = "0c94008c-efd0-483a-9e45-f5cde3df282b",
                name = "The Time Machine",
                pageCount = 200,
                authors = setOf(a10.id)
            )
        )

        val a11 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "12c2d97c-4654-4398-bc0e-c40cf96715c6",
                name = "Aldous Huxley"
            )
        )
        bookService.createOrUpdateBook(
            CreateOrUpdateBookCommand(
                id = "c22ee984-7f74-4158-8bd5-79235b0ad051",
                name = "Brave New World",
                pageCount = 200,
                authors = setOf(a11.id)
            )
        )

        val a12 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "c6aa1cb3-c9bd-47e0-ba1f-12a35027df8d",
                name = "Terry Pratchett"
            )
        )

        val a13 = authorService.createOrUpdateAuthor(
            CreateOrUpdateAuthorCommand(
                id = "38469694-b350-4f1a-89be-1c8fd9aeaf2d",
                name = "Neil Gaiman"
            )
        )

        bookService.createOrUpdateBook(
            CreateOrUpdateBookCommand(
                id = "2f5ac49b-af88-4e72-a549-5f86aff4e549",
                name = "Good Omens",
                pageCount = 490,
                authors = setOf(a12.id, a13.id)
            )
        )
    }
}
