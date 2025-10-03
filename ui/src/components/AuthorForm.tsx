import React, { useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Box,
} from '@mui/material';
import { useCreateAuthor, useUpdateAuthorName } from '../hooks/useGraphQL';

interface AuthorFormProps {
  open: boolean;
  onClose: () => void;
  author?: {
    id: string;
    name: string;
    version: number;
  } | null;
}

const AuthorForm: React.FC<AuthorFormProps> = ({ open, onClose, author }) => {
  const [name, setName] = useState(author?.name || '');
  const [createAuthor] = useCreateAuthor();
  const [updateAuthorName] = useUpdateAuthorName();

  const isEditing = !!author;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    try {
      if (isEditing) {
        await updateAuthorName({
          variables: {
            input: {
              id: author.id,
              name: name,
              version: author.version,
            },
          },
        });
      } else {
        await createAuthor({
          variables: {
            input: {
              name: name,
            },
          },
        });
      }
      
      setName('');
      onClose();
    } catch (error) {
      console.error('Error saving author:', error);
    }
  };

  const handleClose = () => {
    setName('');
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        {isEditing ? 'Edit Author' : 'Add New Author'}
      </DialogTitle>
      <form onSubmit={handleSubmit}>
        <DialogContent>
          <Box sx={{ pt: 1 }}>
            <TextField
              autoFocus
              margin="dense"
              label="Author Name"
              fullWidth
              variant="outlined"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button type="submit" variant="contained">
            {isEditing ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

export default AuthorForm;
