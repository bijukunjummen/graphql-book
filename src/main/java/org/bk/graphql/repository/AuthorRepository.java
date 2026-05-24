package org.bk.graphql.repository;

import org.bk.graphql.entity.AuthorEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AuthorRepository extends CrudRepository<AuthorEntity, String>, PagingAndSortingRepository<AuthorEntity, String> {
    List<AuthorEntity> findAllByIdIn(Collection<String> authorIds);
}

