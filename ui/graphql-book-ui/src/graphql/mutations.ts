import { gql } from '@apollo/client';

export const CREATE_BOOK = gql`
    mutation CreateBook($input: CreateBookInput!) {
        createBook(input: $input) {
            createdBook {
                id
                name
                pageCount
                authors {
                    id
                    name
                }
            }
        }
    }
`;

export const CREATE_AUTHOR = gql`
    mutation CreateAuthor($input: CreateAuthorInput!) {
        createAuthor(input: $input) {
            createdAuthor {
                id
                name
            }
        }
    }
`;
