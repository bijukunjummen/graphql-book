package org.bk.books.service;

import java.util.List;
import org.bk.books.application.BookAuthorManagementService;
import org.bk.books.domain.entity.author.Author;
import org.bk.books.domain.entity.author.AuthorId;
import org.bk.books.domain.entity.book.BookId;
import org.bk.books.service.author.AuthorService;
import org.bk.books.service.author.AuthorServiceCommands.CreateOrUpdateAuthorCommand;
import org.bk.books.service.book.BookCommands.CreateOrUpdateBookCommand;
import org.springframework.stereotype.Service;

@Service
public class SampleDataService {
    private final BookAuthorManagementService bookAuthorManagementService;
    private final AuthorService authorService;

    public SampleDataService(BookAuthorManagementService bookAuthorManagementService, AuthorService authorService) {
        this.bookAuthorManagementService = bookAuthorManagementService;
        this.authorService = authorService;
    }

    public SampleDataLoadResult loadSampleData() {
        Author a1 = createAuthor(AuthorId.parse("37fac9c2-d25d-45b6-945e-2cfe2a5b835b"), "Frank Herbert");
        createBook(BookId.parse("3d8b47d0-52df-480c-9fa2-b6a226d5220b"), "Dune", 200, List.of(a1));

        Author a2 = createAuthor(AuthorId.parse("728d11c9-b72b-46fb-b4a1-0557c3bf26a7"), "Orson Scott");
        createBook(BookId.parse("8028d1eb-0aa3-46fb-aecd-699a5e96e007"), "Ender's Game", 200, List.of(a2));

        Author a3 = createAuthor(AuthorId.parse("18bcf1cb-d0c9-4a90-a53c-ba9041965672"), "Douglas Adams");
        createBook(
                BookId.parse("7f33a782-b811-4da0-8a18-e997aac2ea05"),
                "The Hitchhiker's Guide To The Galaxy",
                200,
                List.of(a3));

        Author a4 = createAuthor(AuthorId.parse("f47621b4-b1c3-4c06-acbc-65ed8e6f9c76"), "Mary Shelley");
        createBook(BookId.parse("5f95e080-87b2-4234-87e7-95bf774099aa"), "Frankenstein", 200, List.of(a4));

        Author a5 = createAuthor(AuthorId.parse("d4c96974-eec5-4221-a24a-e0996c88d9b2"), "Ray Bradbury");
        createBook(BookId.parse("08dd1eda-3e94-46c5-8eab-b43e755d5545"), "Fahrenheit 451", 200, List.of(a5));

        Author a6 = createAuthor(AuthorId.parse("d50657f5-5e00-4117-ab97-e6a45e33e444"), "George Orwell");
        createBook(BookId.parse("8ac017c8-fafb-4f6c-88fe-59651bdc04b8"), "1984", 200, List.of(a6));

        Author a7 = createAuthor(AuthorId.parse("9b8904dd-c662-4a25-8a79-27bb29cac2a2"), "Dan Simmons");
        createBook(BookId.parse("6c513fd3-c95a-4b15-ac9f-640be1e60de0"), "Hyperion", 200, List.of(a7));

        Author a8 = createAuthor(AuthorId.parse("59bc76a3-feb7-42c1-99ee-41174348e005"), "Philip Dick");
        createBook(
                BookId.parse("0418ca6a-6c1f-4faa-b793-48e41bd93cfc"),
                "Do Androids Dream of Electric Sheep",
                200,
                List.of(a8));

        Author a9 = createAuthor(AuthorId.parse("a3d7f148-0058-4fb5-8a85-a0e7e0a04130"), "Isaac Asimov");
        createBook(BookId.parse("a031c746-6a01-43d6-aa20-b35672d47255"), "Foundation", 200, List.of(a9));

        Author a10 = createAuthor(AuthorId.parse("075a9f48-6128-42dd-ab13-c1f45a2052d1"), "H.G. Wells");
        createBook(BookId.parse("0c94008c-efd0-483a-9e45-f5cde3df282b"), "The Time Machine", 200, List.of(a10));

        Author a11 = createAuthor(AuthorId.parse("12c2d97c-4654-4398-bc0e-c40cf96715c6"), "Aldous Huxley");
        createBook(BookId.parse("c22ee984-7f74-4158-8bd5-79235b0ad051"), "Brave New World", 200, List.of(a11));

        Author a12 = createAuthor(AuthorId.parse("c6aa1cb3-c9bd-47e0-ba1f-12a35027df8d"), "Terry Pratchett");
        Author a13 = createAuthor(AuthorId.parse("38469694-b350-4f1a-89be-1c8fd9aeaf2d"), "Neil Gaiman");
        createBook(BookId.parse("2f5ac49b-af88-4e72-a549-5f86aff4e549"), "Good Omens", 490, List.of(a12, a13));

        return new SampleDataLoadResult(13, 12);
    }

    private Author createAuthor(AuthorId authorId, String name) {
        return authorService.createOrUpdateAuthor(new CreateOrUpdateAuthorCommand(authorId, name));
    }

    private void createBook(BookId id, String name, int pageCount, List<Author> authors) {
        bookAuthorManagementService.createOrUpdateBook(new CreateOrUpdateBookCommand(
                id, name, pageCount, authors.stream().map(Author::id).toList()));
    }
}
