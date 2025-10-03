import { gql } from '@apollo/client';

// Author Queries
export const FIND_AUTHORS = gql`
  query FindAuthors($first: Int, $after: String, $sort: [Sort]) {
    findAuthors(first: $first, after: $after, sort: $sort) {
      edges {
        cursor
        node {
          id
          name
          version
        }
      }
      pageInfo {
        hasNextPage
        hasPreviousPage
        startCursor
        endCursor
      }
      totalCount
    }
  }
`;

export const FIND_AUTHOR_BY_ID = gql`
  query FindAuthorById($id: ID!) {
    findAuthorById(id: $id) {
      id
      name
      version
    }
  }
`;

// Book Queries
export const FIND_BOOKS = gql`
  query FindBooks($first: Int, $after: String, $sort: [Sort]) {
    findBooks(first: $first, after: $after, sort: $sort) {
      edges {
        cursor
        node {
          id
          name
          pageCount
          version
          authors {
            id
            name
            version
          }
        }
      }
      pageInfo {
        hasNextPage
        hasPreviousPage
        startCursor
        endCursor
      }
      totalCount
    }
  }
`;

export const FIND_BOOK_BY_ID = gql`
  query FindBookById($id: ID!) {
    findBookById(id: $id) {
      id
      name
      pageCount
      version
      authors {
        id
        name
        version
      }
    }
  }
`;

// Author Mutations
export const CREATE_AUTHOR = gql`
  mutation CreateAuthor($input: CreateAuthorInput!) {
    createAuthor(input: $input) {
      createdAuthor {
        id
        name
        version
      }
    }
  }
`;

export const UPDATE_AUTHOR_NAME = gql`
  mutation UpdateAuthorName($input: UpdateAuthorNameInput!) {
    updateAuthorName(input: $input) {
      updatedAuthor {
        id
        name
        version
      }
    }
  }
`;

// Book Mutations
export const CREATE_BOOK = gql`
  mutation CreateBook($input: CreateBookInput!) {
    createBook(input: $input) {
      createdBook {
        id
        name
        pageCount
        version
        authors {
          id
          name
          version
        }
      }
    }
  }
`;

export const UPDATE_BOOK_NAME = gql`
  mutation UpdateBookName($input: UpdateBookNameInput!) {
    updateBookName(input: $input) {
      book {
        id
        name
        pageCount
        version
        authors {
          id
          name
          version
        }
      }
    }
  }
`;

