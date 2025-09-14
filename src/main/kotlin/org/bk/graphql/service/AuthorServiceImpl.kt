package org.bk.graphql.service

import org.bk.graphql.domain.Author
import org.bk.graphql.domain.AuthorId
import org.bk.graphql.entity.AuthorEntity
import org.bk.graphql.repository.AuthorRepository
import org.bk.graphql.service.exception.DomainException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

@Service
class AuthorServiceImpl(private val authorRepository: AuthorRepository) : AuthorService {
    override fun createAuthor(command: CreateAuthorCommand): Author {
        val author = AuthorEntity(
            id = UUID.randomUUID().toString(),
            name = command.name,
            version = 0
        )
        val savedAuthor = authorRepository.save(author)
        return savedAuthor.toModel()
    }

    override fun createOrUpdateAuthor(command: CreateOrUpdateAuthorCommand): Author {
        val author: Optional<AuthorEntity> = authorRepository.findById(command.id)

        author.ifPresentOrElse({
            if (command.version != 0) {
                val updatedAuthor = it.copy(
                    name = command.name,
                    version = command.version
                )
                authorRepository.save(updatedAuthor)
            }
        }, {
            val newAuthor = AuthorEntity(
                id = command.id,
                name = command.name,
                version = 0
            )
            authorRepository.save(newAuthor)
        })
        val authorEntity: AuthorEntity =
            authorRepository.findById(command.id).orElseThrow()
        return authorEntity.toModel()
    }

    override fun updateAuthor(command: UpdateAuthorCommand): Author {
        val author = authorRepository.findById(command.id)
            .orElseThrow { DomainException("Author not found") }

        val updatedAuthor = author.copy(
            name = command.name,
            version = command.version
        )
        val updatedAuthorEntity: AuthorEntity =  authorRepository.save(updatedAuthor)
        return updatedAuthorEntity.toModel()
    }

    @Transactional
    override fun updateAuthorName(command: UpdateAuthorNameCommand): Author {
        val author = authorRepository.findById(command.id)
            .orElseThrow { DomainException("Author not found") }
        val updatedAuthor = author.copy(name = command.name, version = command.version)
        val updatedEntity: AuthorEntity = authorRepository.save(updatedAuthor)
        return updatedEntity.toModel()
    }

    override fun getAuthors(query: GetAuthorsQuery): Page<Author> {
        return authorRepository
            .findAll(Pageable.ofSize(query.size)
                .withPage(query.page))
        .map { it.toModel() }
    }

    override fun getAuthor(query: ById<AuthorId>): Author {
        return authorRepository
            .findById(query.id.id)
            .orElseThrow { DomainException("Author not found") }
        .toModel()
    }

    override fun getAuthors(query: ByIds<AuthorId>): List<Author> {
        return authorRepository.findAllById(query.ids.map { authorId -> authorId.id }).map { it.toModel() }
    }

    override fun getAuthors(pageable: Pageable): Page<Author> {
        return authorRepository.findAll(pageable)
            .map { it.toModel() }
    }
}