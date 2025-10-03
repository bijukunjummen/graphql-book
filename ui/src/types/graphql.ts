// Manual GraphQL types
export interface Author {
  id: string;
  name: string;
  version: number;
}

export interface Book {
  id: string;
  name: string;
  pageCount?: number | null;
  version: number;
  authors?: Author[] | null;
}

export interface AuthorEdge {
  cursor: string;
  node?: Author | null;
}

export interface BookEdge {
  cursor: string;
  node?: Book | null;
}

export interface PageInfo {
  hasNextPage: boolean;
  hasPreviousPage: boolean;
  startCursor?: string | null;
  endCursor?: string | null;
}

export interface AuthorConnection {
  edges?: AuthorEdge[] | null;
  pageInfo: PageInfo;
  totalCount?: number | null;
}

export interface BookConnection {
  edges?: BookEdge[] | null;
  pageInfo: PageInfo;
  totalCount?: number | null;
}

export interface CreateAuthorInput {
  name: string;
}

export interface UpdateAuthorNameInput {
  id: string;
  name?: string | null;
  version: number;
}

export interface CreateBookInput {
  name: string;
  pageCount?: number | null;
  authors: string[];
}

export interface UpdateBookNameInput {
  id: string;
  name?: string | null;
  version: number;
}

export interface CreateAuthorPayload {
  createdAuthor?: Author | null;
}

export interface UpdateAuthorNamePayload {
  updatedAuthor?: Author | null;
}

export interface CreateBookPayload {
  createdBook?: Book | null;
}

export interface UpdateBookNamePayload {
  book?: Book | null;
}

