import React, { useState } from 'react';
import {
  Typography,
  Box,
  Button,
  Paper,
  Container,
} from '@mui/material';
import { Add } from '@mui/icons-material';
import AuthorList from '../components/AuthorList';
import AuthorForm from '../components/AuthorForm';

const AuthorsPage: React.FC = () => {
  const [formOpen, setFormOpen] = useState(false);

  const handleClose = () => {
    setFormOpen(false);
  };

  return (
    <Container maxWidth="lg">
      <Box sx={{ mb: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h4" component="h1">
            Authors
          </Typography>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => setFormOpen(true)}
            size="large"
          >
            Add Author
          </Button>
        </Box>
        
        <Paper elevation={1}>
          <AuthorList />
        </Paper>
      </Box>

      <AuthorForm
        open={formOpen}
        onClose={handleClose}
      />
    </Container>
  );
};

export default AuthorsPage;

