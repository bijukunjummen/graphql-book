package org.bk.graphql.service

import org.bk.graphql.domain.Author
import org.bk.graphql.domain.AuthorId
import org.bk.graphql.entity.AuthorEntity
import org.bk.graphql.repository.AuthorRepository
import org.bk.graphql.service.exception.DomainException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID

@Service
class AuthorServiceImpl(private val authorRepository: AuthorRepository) : AuthorService {
    override fun createAuthor(createAuthorCommand: CreateAuthorCommand): Author {
        val author = AuthorEntity(
            id = UUID.randomUUID().toString(),
            name = createAuthorCommand.name,
            version = 0
        )
        val savedAuthor = authorRepository.save(author)
        return savedAuthor.toModel()
    }

    override fun createOrUpdateAuthor(createOrUpdateAuthorCommand: CreateOrUpdateAuthorCommand): Author {
        val author: Optional<AuthorEntity> = authorRepository.findById(createOrUpdateAuthorCommand.id)

        author.ifPresentOrElse({
            if (createOrUpdateAuthorCommand.version != 0) {
                val updatedAuthor = it.copy(
                    name = createOrUpdateAuthorCommand.name,
                    version = createOrUpdateAuthorCommand.version
                )
                authorRepository.save(updatedAuthor)
            }
        }, {
            val newAuthor = AuthorEntity(
                id = createOrUpdateAuthorCommand.id,
                name = createOrUpdateAuthorCommand.name,
                version = 0
            )
            authorRepository.save(newAuthor)
        })
        val authorEntity: AuthorEntity =
            authorRepository.findById(createOrUpdateAuthorCommand.id).orElseThrow()
        return authorEntity.toModel()
    }

    override fun updateAuthor(updateAuthorCommand: UpdateAuthorCommand): Author {
        val author = authorRepository.findById(updateAuthorCommand.id)
            .orElseThrow { DomainException("Author not found") }

        val updatedAuthor = author.copy(
            name = updateAuthorCommand.name,
            version = updateAuthorCommand.version
        )
        val updatedAuthorEntity: AuthorEntity =  authorRepository.save(updatedAuthor)
        return updatedAuthorEntity.toModel()
    }

    override fun getAuthors(getAuthorsQuery: GetAuthorsQuery): Page<Author> {
        return authorRepository
            .findAll(Pageable.ofSize(getAuthorsQuery.size)
                .withPage(getAuthorsQuery.page))
        .map { it.toModel() }
    }

    override fun getAuthor(getAuthorQuery: ById<AuthorId>): Author {
        return authorRepository
            .findById(getAuthorQuery.id.id)
            .orElseThrow { DomainException("Author not found") }
        .toModel()
    }

    override fun getAuthors(ids: ByIds<AuthorId>): List<Author> {
        return authorRepository.findAllById(ids.ids.map { authorId -> authorId.id }).map { it.toModel() }
    }
}