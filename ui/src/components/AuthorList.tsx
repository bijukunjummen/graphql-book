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
import { useFindAuthors } from '../hooks/useGraphQL';
import AuthorForm from './AuthorForm';

const AuthorList: React.FC = () => {
  const [formOpen, setFormOpen] = useState(false);
  const [editingAuthor, setEditingAuthor] = useState<{
    id: string;
    name: string;
    version: number;
  } | null>(null);

  const { data, loading, error, refetch } = useFindAuthors(100);

  const handleEdit = (author: { id: string; name: string; version: number }) => {
    setEditingAuthor(author);
    setFormOpen(true);
  };

  const handleClose = () => {
    setFormOpen(false);
    setEditingAuthor(null);
    refetch();
  };

  if (loading) return <Typography>Loading authors...</Typography>;
  if (error) return <Typography color="error">Error loading authors: {error.message}</Typography>;

  const authors = data?.findAuthors?.edges?.map((edge: any) => edge?.node).filter(Boolean) || [];

  return (
    <Box>
      <AuthorForm
        open={formOpen}
        onClose={handleClose}
        author={editingAuthor}
      />
      
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Version</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {authors.length === 0 ? (
              <TableRow>
                <TableCell colSpan={3} align="center">
                  <Typography color="text.secondary">
                    No authors found. Add your first author!
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              authors.map((author: any) => (
                <TableRow key={author?.id}>
                  <TableCell>
                    <Typography variant="body1">
                      {author?.name}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Chip 
                      label={`v${author?.version}`} 
                      size="small" 
                      color="primary" 
                      variant="outlined"
                    />
                  </TableCell>
                  <TableCell align="right">
                    <IconButton
                      onClick={() => handleEdit({
                        id: author?.id || '',
                        name: author?.name || '',
                        version: author?.version || 0,
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

export default AuthorList;
