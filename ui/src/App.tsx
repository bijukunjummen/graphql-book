import { ApolloProvider } from '@apollo/client/react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { apolloClient } from './lib/apollo-client';
import Layout from './components/Layout';
import AuthorsPage from './pages/AuthorsPage';
import BooksPage from './pages/BooksPage';
import HomePage from './pages/HomePage';

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

function App() {
  return (
    <ApolloProvider client={apolloClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Router>
          <Layout>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/authors" element={<AuthorsPage />} />
              <Route path="/books" element={<BooksPage />} />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </Layout>
        </Router>
      </ThemeProvider>
    </ApolloProvider>
  );
}

export default App;