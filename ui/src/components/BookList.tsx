import React, { useState } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Typography,
  Box,
  Chip,
} from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';
import { useFindBooks } from '../hooks/useGraphQL';
import BookForm from './BookForm';

const BookList: React.FC = () => {
  const [formOpen, setFormOpen] = useState(false);
  const [editingBook, setEditingBook] = useState<{
    id: string;
    name: string;
    pageCount?: number | null;
    version: number;
    authors?: Array<{ id: string; name: string }> | null;
  } | null>(null);

  const { data, loading, error, refetch } = useFindBooks(100);

  const handleEdit = (book: {
    id: string;
    name: string;
    pageCount?: number | null;
    version: number;
    authors?: Array<{ id: string; name: string }> | null;
  }) => {
    setEditingBook(book);
    setFormOpen(true);
  };

  const handleClose = () => {
    setFormOpen(false);
    setEditingBook(null);
    refetch();
  };

  if (loading) return <Typography>Loading books...</Typography>;
  if (error) return <Typography color="error">Error loading books: {error.message}</Typography>;

  const books = data?.findBooks?.edges?.map((edge: any) => edge?.node).filter(Boolean) || [];

  return (
    <Box>
      <BookForm
        open={formOpen}
        onClose={handleClose}
        book={editingBook}
      />
      
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Page Count</TableCell>
              <TableCell>Authors</TableCell>
              <TableCell>Version</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {books.length === 0 ? (
              <TableRow>
                <TableCell colSpan={5} align="center">
                  <Typography color="text.secondary">
                    No books found. Add your first book!
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              books.map((book: any) => (
                <TableRow key={book?.id}>
                  <TableCell>
                    <Typography variant="body1">
                      {book?.name}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2" color="text.secondary">
                      {book?.pageCount || 'N/A'}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                      {book?.authors?.map((author: any) => (
                        <Chip
                          key={author.id}
                          label={author.name}
                          size="small"
                          variant="outlined"
                          color="secondary"
                        />
                      )) || <Typography variant="body2" color="text.secondary">No authors</Typography>}
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Chip 
                      label={`v${book?.version}`} 
                      size="small" 
                      color="primary" 
                      variant="outlined"
                    />
                  </TableCell>
                  <TableCell align="right">
                    <IconButton
                      onClick={() => handleEdit({
                        id: book?.id || '',
                        name: book?.name || '',
                        pageCount: book?.pageCount,
                        version: book?.version || 0,
                        authors: book?.authors,
                      })}
                      color="primary"
                    >
                      <Edit />
                    </IconButton>
                    <IconButton color="error" disabled>
                      <Delete />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default BookList;
