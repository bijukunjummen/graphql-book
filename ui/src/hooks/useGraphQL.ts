import { useQuery, useMutation } from '@apollo/client/react';
import { 
  FIND_AUTHORS, 
  FIND_BOOKS, 
  CREATE_AUTHOR, 
  UPDATE_AUTHOR_NAME, 
  CREATE_BOOK, 
  UPDATE_BOOK_NAME 
} from '../graphql/operations';
import type { 
  Author, 
  Book, 
  AuthorConnection, 
  BookConnection,
  CreateAuthorInput,
  UpdateAuthorNameInput,
  CreateBookInput,
  UpdateBookNameInput
} from '../types/graphql';

export const useFindAuthors = (first?: number, after?: string) => {
  return useQuery<{ findAuthors: AuthorConnection }>(FIND_AUTHORS, {
    variables: { first: first || 100, after },
  });
};

export const useFindBooks = (first?: number, after?: string) => {
  return useQuery<{ findBooks: BookConnection }>(FIND_BOOKS, {
    variables: { first: first || 100, after },
  });
};

export const useCreateAuthor = () => {
  return useMutation<{ createAuthor: { createdAuthor?: Author | null } }, { input: CreateAuthorInput }>(CREATE_AUTHOR);
};

export const useUpdateAuthorName = () => {
  return useMutation<{ updateAuthorName: { updatedAuthor?: Author | null } }, { input: UpdateAuthorNameInput }>(UPDATE_AUTHOR_NAME);
};

export const useCreateBook = () => {
  return useMutation<{ createBook: { createdBook?: Book | null } }, { input: CreateBookInput }>(CREATE_BOOK);
};

export const useUpdateBookName = () => {
  return useMutation<{ updateBookName: { book?: Book | null } }, { input: UpdateBookNameInput }>(UPDATE_BOOK_NAME);
};
