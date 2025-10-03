import React from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Container,
  Box,
  Button,
} from '@mui/material';
import { Link, useLocation } from 'react-router-dom';

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const location = useLocation();

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            GraphQL Book Manager
          </Typography>
          <Button
            color="inherit"
            component={Link}
            to="/"
            sx={{ 
              mr: 2,
              backgroundColor: location.pathname === '/' ? 'rgba(255,255,255,0.1)' : 'transparent'
            }}
          >
            Home
          </Button>
          <Button
            color="inherit"
            component={Link}
            to="/authors"
            sx={{ 
              mr: 2,
              backgroundColor: location.pathname === '/authors' ? 'rgba(255,255,255,0.1)' : 'transparent'
            }}
          >
            Authors
          </Button>
          <Button
            color="inherit"
            component={Link}
            to="/books"
            sx={{ 
              backgroundColor: location.pathname === '/books' ? 'rgba(255,255,255,0.1)' : 'transparent'
            }}
          >
            Books
          </Button>
        </Toolbar>
      </AppBar>
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        {children}
      </Container>
    </Box>
  );
};

export default Layout;

