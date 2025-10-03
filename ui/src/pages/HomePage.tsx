import React from 'react';
import {
  Typography,
  Box,
  Card,
  CardContent,
  Button,
} from '@mui/material';
import { Link } from 'react-router-dom';
import { Book, Person } from '@mui/icons-material';

const HomePage: React.FC = () => {
  return (
    <Box>
      <Typography variant="h3" component="h1" gutterBottom align="center">
        Welcome to GraphQL Book Manager
      </Typography>
      <Typography variant="h6" component="p" gutterBottom align="center" color="text.secondary">
        Manage your books and authors with a modern GraphQL-powered interface
      </Typography>
      
      <Box sx={{ display: 'flex', gap: 3, mt: 4, flexDirection: { xs: 'column', md: 'row' } }}>
        <Card sx={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
          <CardContent sx={{ flexGrow: 1, textAlign: 'center' }}>
            <Person sx={{ fontSize: 60, color: 'primary.main', mb: 2 }} />
            <Typography variant="h5" component="h2" gutterBottom>
              Authors
            </Typography>
            <Typography variant="body1" color="text.secondary" paragraph>
              Create, update, and manage author information. Add new authors to your collection
              and keep their details up to date.
            </Typography>
            <Button
              variant="contained"
              component={Link}
              to="/authors"
              size="large"
              sx={{ mt: 2 }}
            >
              Manage Authors
            </Button>
          </CardContent>
        </Card>
        
        <Card sx={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
          <CardContent sx={{ flexGrow: 1, textAlign: 'center' }}>
            <Book sx={{ fontSize: 60, color: 'secondary.main', mb: 2 }} />
            <Typography variant="h5" component="h2" gutterBottom>
              Books
            </Typography>
            <Typography variant="body1" color="text.secondary" paragraph>
              Add new books, assign authors, and track page counts. Build your personal
              library with detailed book information.
            </Typography>
            <Button
              variant="contained"
              component={Link}
              to="/books"
              size="large"
              sx={{ mt: 2 }}
              color="secondary"
            >
              Manage Books
            </Button>
          </CardContent>
        </Card>
      </Box>
    </Box>
  );
};

export default HomePage;
