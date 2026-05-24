import React from 'react';
import { Stack, Typography } from '@mui/material';
import BookList from '../components/BookList';

const BooksPage: React.FC = () => {
  return (
    <Stack spacing={3}>
      <Typography variant="h4" component="h1">
        Books
      </Typography>
      <BookList title="Book Inventory" />
    </Stack>
  );
};

export default BooksPage;
