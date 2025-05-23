package org.bk.graphql.service

import org.bk.graphql.model.Author
import org.bk.graphql.repository.AuthorRepository
import org.bk.graphql.service.exception.DomainException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuthorServiceImpl(private val authorRepository: AuthorRepository): AuthorService {
    override fun createAuthor(createAuthorCommand: CreateAuthorCommand): Author {
        val author = Author(
            id = UUID.randomUUID().toString(),
            name = createAuthorCommand.name,
            version = 0
        )
        return authorRepository.save(author)
    }

    override fun createOrUpdateAuthor(createOrUpdateAuthorCommand: CreateOrUpdateAuthorCommand): Author {
        val author = authorRepository.findById(createOrUpdateAuthorCommand.id)

        author.ifPresentOrElse({
            if (createOrUpdateAuthorCommand.version != 0) {
                val updatedAuthor = it.copy(
                    name = createOrUpdateAuthorCommand.name,
                    version = createOrUpdateAuthorCommand.version
                )
                authorRepository.save(updatedAuthor)
            }
        }, {
            val newAuthor = Author(
                id = createOrUpdateAuthorCommand.id,
                name = createOrUpdateAuthorCommand.name,
                version = 0
            )
            authorRepository.save(newAuthor)
        })
        return authorRepository.findById(createOrUpdateAuthorCommand.id).orElseThrow()
    }

    override fun updateAuthor(updateAuthorCommand: UpdateAuthorCommand): Author {
        val author = authorRepository.findById(updateAuthorCommand.id)
            .orElseThrow { DomainException("Author not found") }

        val updatedAuthor = author.copy(
            name = updateAuthorCommand.name,
            version = updateAuthorCommand.version
        )
        return authorRepository.save(updatedAuthor)
    }

    override fun getAuthors(getAuthorsQuery: GetAuthorsQuery): Page<Author> {
        return authorRepository.findAll(Pageable.ofSize(getAuthorsQuery.size).withPage(getAuthorsQuery.page))
    }

    override fun getAuthor(getAuthorQuery: ById): Author {
        return authorRepository.findById(getAuthorQuery.id).orElseThrow { DomainException("Author not found") }
    }

    override fun getAuthors(ids: ByIds): List<Author> {
        return authorRepository.findAllById(ids.ids).toList()
    }
}