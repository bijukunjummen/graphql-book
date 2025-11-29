package org.bk.graphql;

import org.bk.graphql.service.AuthorService;
import org.bk.graphql.service.BookService;
import org.bk.graphql.service.CreateOrUpdateAuthorCommand;
import org.bk.graphql.service.CreateOrUpdateBookCommand;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.util.Set;

@SpringBootApplication
public class GraphqlBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlBookApplication.class, args);
    }
}

@Service
class LoadDb implements ApplicationRunner {
    private final BookService bookService;
    private final AuthorService authorService;

    public LoadDb(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @Override
    public void run(ApplicationArguments args) {
        var a1 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "37fac9c2-d25d-45b6-945e-2cfe2a5b835b",
                "Frank Herbert"
            )
        );
        bookService.createOrUpdateBook(
            new CreateOrUpdateBookCommand(
                "3d8b47d0-52df-480c-9fa2-b6a226d5220b",
                "Dune",
                200,
                Set.of(a1.id())
            )
        );

        var a2 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "728d11c9-b72b-46fb-b4a1-0557c3bf26a7",
                "Orson Scott"
            )
        );
        bookService.createOrUpdateBook(
            new CreateOrUpdateBookCommand(
                "8028d1eb-0aa3-46fb-aecd-699a5e96e007",
                "Ender's Game",
                200,
                Set.of(a2.id())
            )
        );

        var a3 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "18bcf1cb-d0c9-4a90-a53c-ba9041965672",
                "Douglas Adams"
            )
        );
        bookService.createOrUpdateBook(
            new CreateOrUpdateBookCommand(
                "7f33a782-b811-4da0-8a18-e997aac2ea05",
                "The Hitchhiker's Guide To The Galaxy",
                200,
                Set.of(a3.id())
            )
        );

        var a4 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "f47621b4-b1c3-4c06-acbc-65ed8e6f9c76",
                "Mary Shelley"
            )
        );
        bookService.createOrUpdateBook(
            new CreateOrUpdateBookCommand(
                "5f95e080-87b2-4234-87e7-95bf774099aa",
                "Frankenstein",
                200,
                Set.of(a4.id())
            )
        );

        var a5 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "d4c96974-eec5-4221-a24a-e0996c88d9b2",
                "Ray Bradbury"
            )
        );
        bookService.createOrUpdateBook(
            new CreateOrUpdateBookCommand(
                "08dd1eda-3e94-46c5-8eab-b43e755d5545",
                "Fahrenheit 451",
                200,
                Set.of(a5.id())
            )
        );

        var a6 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "d50657f5-5e00-4117-ab97-e6a45e33e444",
                "George Orwell"
            )
        );
        bookService.createOrUpdateBook(
            new CreateOrUpdateBookCommand(
                "8ac017c8-fafb-4f6c-88fe-59651bdc04b8",
                "1984",
                200,
                Set.of(a6.id())
            )
        );

        var a7 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "9b8904dd-c662-4a25-8a79-27bb29cac2a2",
                "Dan Simmons"
            )
        );
        bookService.createOrUpdateBook(
            new CreateOrUpdateBookCommand(
                "6c513fd3-c95a-4b15-ac9f-640be1e60de0",
                "Hyperion",
                200,
                Set.of(a7.id())
            )
        );

        var a8 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "59bc76a3-feb7-42c1-99ee-41174348e005",
                "Philip Dick"
            )
        );
        bookService.createOrUpdateBook(
            new CreateOrUpdateBookCommand(
                "0418ca6a-6c1f-4faa-b793-48e41bd93cfc",
                "Do Androids Dream of Electric Sheep",
                200,
                Set.of(a8.id())
            )
        );

        var a9 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "a3d7f148-0058-4fb5-8a85-a0e7e0a04130",
                "Isaac Asimov"
            )
        );
        bookService.createOrUpdateBook(
            new CreateOrUpdateBookCommand(
                "a031c746-6a01-43d6-aa20-b35672d47255",
                "Foundation",
                200,
                Set.of(a9.id())
            )
        );

        var a10 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "075a9f48-6128-42dd-ab13-c1f45a2052d1",
                "H.G. Wells"
            )
        );
        bookService.createOrUpdateBook(
            new CreateOrUpdateBookCommand(
                "0c94008c-efd0-483a-9e45-f5cde3df282b",
                "The Time Machine",
                200,
                Set.of(a10.id())
            )
        );

        var a11 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "12c2d97c-4654-4398-bc0e-c40cf96715c6",
                "Aldous Huxley"
            )
        );
        bookService.createOrUpdateBook(
            new CreateOrUpdateBookCommand(
                "c22ee984-7f74-4158-8bd5-79235b0ad051",
                "Brave New World",
                200,
                Set.of(a11.id())
            )
        );

        var a12 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "c6aa1cb3-c9bd-47e0-ba1f-12a35027df8d",
                "Terry Pratchett"
            )
        );

        var a13 = authorService.createOrUpdateAuthor(
            new CreateOrUpdateAuthorCommand(
                "38469694-b350-4f1a-89be-1c8fd9aeaf2d",
                "Neil Gaiman"
            )
        );

        bookService.createOrUpdateBook(
            new CreateOrUpdateBookCommand(
                "2f5ac49b-af88-4e72-a549-5f86aff4e549",
                "Good Omens",
                490,
                Set.of(a12.id(), a13.id())
            )
        );
    }
}

