import { useState } from 'react';
import { useMutation, useQuery } from '@apollo/client/react';
import { CREATE_BOOK } from '../graphql/mutations';
import { GET_BOOKS, GET_AUTHORS } from '../graphql/queries';
import {
  Box,
  Button,
  TextField,
  Typography,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  OutlinedInput,
  Chip,
  Alert,
  CircularProgress,
  Stack,
} from '@mui/material';
import type { SelectChangeEvent } from '@mui/material';

interface Author {
  id: string;
  name: string;
}

interface AuthorsData {
  findAuthors: {
    edges: Array<{
      node: Author;
    }>;
  };
}


interface CreateBookResponse {
  createBook: {
    book: {
      id: string;
      name: string;
      pageCount: number;
      authors: Author[];
    };
  };
}

export const BookForm = () => {
  const [name, setName] = useState('');
  const [pageCount, setPageCount] = useState('');
  const [selectedAuthorIds, setSelectedAuthorIds] = useState<string[]>([]);

  const { loading: loadingAuthors, data: authorsData } = useQuery<AuthorsData>(GET_AUTHORS);

  const [createBook, { loading: bookLoading, error: bookError }] = useMutation<CreateBookResponse>(CREATE_BOOK, {
    refetchQueries: [{ query: GET_BOOKS, variables: { first: 10 } }],
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await createBook({
        variables: {
          input: {
            name,
            pageCount: parseInt(pageCount),
            authors: selectedAuthorIds,
          },
        },
      });

      setName('');
      setPageCount('');
      setSelectedAuthorIds([]);
    } catch (error) {
      console.error('Error creating book:', error);
    }
  };

  const handleAuthorChange = (event: React.ChangeEvent<{ value: unknown }> | SelectChangeEvent<string[]>) => {
    const value = event.target.value;
    setSelectedAuthorIds(Array.isArray(value) ? value : []);
  };

  const isLoading = bookLoading || loadingAuthors;
  const error = bookError;

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom>Create New Book</Typography>
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error.message}
        </Alert>
      )}
      <Stack spacing={2} sx={{ maxWidth: 400 }}>
        {isLoading && (
          <Box sx={{ display: 'flex', justifyContent: 'center' }}>
            <CircularProgress />
          </Box>
        )}
        <TextField
          label="Book Name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
        <TextField
          label="Page Count"
          type="number"
          value={pageCount}
          onChange={(e) => setPageCount(e.target.value)}
          required
        />
          <FormControl fullWidth>
            <InputLabel id="authors-label">Authors</InputLabel>
            <Select
              labelId="authors-label"
              multiple
              value={selectedAuthorIds}
              onChange={handleAuthorChange}
              input={<OutlinedInput label="Authors" />}
              renderValue={(selected) => (
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                  {(selected as string[]).map((authorId) => {
                    const author = authorsData?.findAuthors.edges.find(
                      (edge) => edge.node.id === authorId
                    )?.node;
                    return author ? (
                      <Chip key={author.id} label={author.name} />
                    ) : null;
                  })}
                </Box>
              )}
            >
              {loadingAuthors ? (
                <MenuItem disabled>
                  <CircularProgress size={20} sx={{ mr: 1 }} />
                  Loading authors...
                </MenuItem>
              ) : (
                authorsData?.findAuthors.edges.map(({ node: author }) => (
                  <MenuItem key={author.id} value={author.id}>
                    {author.name}
                  </MenuItem>
                ))
              )}
            </Select>
          </FormControl>
        <Button
          type="submit"
          variant="contained"
          color="primary"
          disabled={isLoading || !name || !pageCount || selectedAuthorIds.length === 0}
        >
          Create Book
        </Button>
      </Stack>
    </Box>
  );
};
