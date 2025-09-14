import { ApolloProvider } from '@apollo/client/react';
import { ErrorBoundary } from './components/ErrorBoundary';
import { CssBaseline, Container, AppBar, Toolbar, Typography, Box } from '@mui/material';
import { client } from './lib/apollo-client';
import { BookList } from './components/BookList';
import { BookForm } from './components/BookForm';

function App() {
  return (
    <ErrorBoundary>
      <ApolloProvider client={client}>
        <CssBaseline />
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6">GraphQL Book Manager</Typography>
          </Toolbar>
        </AppBar>
        <Container>
          <Box sx={{ mt: 4 }}>
            <BookForm />
            <BookList />
          </Box>
        </Container>
      </ApolloProvider>
    </ErrorBoundary>
  );
}

export default App;
