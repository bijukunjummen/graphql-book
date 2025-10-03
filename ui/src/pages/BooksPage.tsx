import React, { useState } from 'react';
import {
  Typography,
  Box,
  Button,
  Paper,
  Container,
} from '@mui/material';
import { Add } from '@mui/icons-material';
import BookList from '../components/BookList';
import BookForm from '../components/BookForm';

const BooksPage: React.FC = () => {
  const [formOpen, setFormOpen] = useState(false);

  const handleClose = () => {
    setFormOpen(false);
  };

  return (
    <Container maxWidth="lg">
      <Box sx={{ mb: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h4" component="h1">
            Books
          </Typography>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => setFormOpen(true)}
            size="large"
            color="secondary"
          >
            Add Book
          </Button>
        </Box>
        
        <Paper elevation={1}>
          <BookList />
        </Paper>
      </Box>

      <BookForm
        open={formOpen}
        onClose={handleClose}
      />
    </Container>
  );
};

export default BooksPage;

