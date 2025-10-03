# GraphQL Book Manager UI

A modern React TypeScript application for managing books and authors using GraphQL.

## Features

- **Authors Management**: Create, read, and update author information
- **Books Management**: Create, read, and update book information with author associations
- **Modern UI**: Built with Material-UI for a clean, responsive interface
- **Type Safety**: Full TypeScript support with generated GraphQL types
- **Real-time Updates**: Apollo Client for efficient data fetching and caching

## Tech Stack

- **React 19** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **Apollo Client** - GraphQL client
- **Material-UI** - UI component library
- **React Router** - Client-side routing

## Getting Started

### Prerequisites

- Node.js (v18 or higher)
- The GraphQL backend server running on `http://localhost:8080`

### Installation

1. Install dependencies:
   ```bash
   npm install
   ```

2. Generate TypeScript types from GraphQL schema:
   ```bash
   npm run codegen
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

4. Open your browser and navigate to `http://localhost:5173`

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint
- `npm run codegen` - Generate TypeScript types from GraphQL schema

## Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── AuthorForm.tsx   # Author creation/editing form
│   ├── AuthorList.tsx   # Author listing component
│   ├── BookForm.tsx     # Book creation/editing form
│   ├── BookList.tsx     # Book listing component
│   └── Layout.tsx       # Main layout wrapper
├── generated/           # Auto-generated GraphQL types
│   └── graphql.ts
├── graphql/             # GraphQL operations
│   └── operations.ts
├── lib/                 # Utility libraries
│   └── apollo-client.ts # Apollo Client configuration
├── pages/               # Page components
│   ├── AuthorsPage.tsx  # Authors management page
│   ├── BooksPage.tsx    # Books management page
│   └── HomePage.tsx     # Home page
└── App.tsx              # Main application component
```

## GraphQL Operations

The application supports the following GraphQL operations:

### Authors
- `findAuthors` - List all authors with pagination
- `findAuthorById` - Get author by ID
- `createAuthor` - Create new author
- `updateAuthorName` - Update author name

### Books
- `findBooks` - List all books with pagination
- `findBookById` - Get book by ID
- `createBook` - Create new book
- `updateBookName` - Update book name

## Development

### Adding New Features

1. Update GraphQL operations in `src/graphql/operations.ts`
2. Run `npm run codegen` to generate new types
3. Create or update components as needed
4. Test your changes

### Code Generation

The project uses GraphQL Code Generator to automatically create TypeScript types and React hooks from the GraphQL schema. The configuration is in `codegen.yml`.

## Backend Integration

This UI connects to a Spring Boot GraphQL backend. Make sure the backend is running on `http://localhost:8080` before starting the development server.

The GraphQL endpoint is configured in `src/lib/apollo-client.ts`.