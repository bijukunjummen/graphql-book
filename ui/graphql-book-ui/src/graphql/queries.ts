import { gql } from '@apollo/client';

export const GET_BOOKS = gql`
  query GetBooks($first: Int, $after: String, $sort: [Sort]) {
    findBooks(first: $first, after: $after, sort: $sort) {
      edges {
        cursor
        node {
          id
          name
          pageCount
          authors {
            id
            name
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

export const GET_AUTHORS = gql`
  query GetAuthors {
    findAuthors {
      edges {
        node {
          id
          name
        }
      }
    }
  }
`;
