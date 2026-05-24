import React, { useEffect, useState } from 'react';
import {
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Box,
  Stack,
  Typography,
} from '@mui/material';
import { useApolloClient } from '@apollo/client/react';
import { PersonAdd, DriveFileRenameOutline } from '@mui/icons-material';
import { useCreateAuthor, useUpdateAuthorName } from '../hooks/useGraphQL';

interface AuthorFormProps {
  open: boolean;
  onClose: () => void;
  onSaved?: () => void;
  author?: {
    id: string;
    name: string;
    version: number;
  } | null;
}

const AuthorForm: React.FC<AuthorFormProps> = ({ open, onClose, onSaved, author }) => {
  const [name, setName] = useState(author?.name || '');
  const [formError, setFormError] = useState<string | null>(null);
  const [createAuthor, { loading: creating }] = useCreateAuthor();
  const [updateAuthorName, { loading: updating }] = useUpdateAuthorName();
  const apolloClient = useApolloClient();

  const isEditing = !!author;
  const saving = creating || updating;

  useEffect(() => {
    setName(author?.name || '');
    setFormError(null);
  }, [author]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const trimmedName = name.trim();
    if (!trimmedName) {
      setFormError('Author name is required.');
      return;
    }
    
    try {
      if (isEditing) {
        await updateAuthorName({
          variables: {
            input: {
              id: author.id,
              name: trimmedName,
              version: author.version,
            },
          },
        });
      } else {
        await createAuthor({
          variables: {
            input: {
              name: trimmedName,
            },
          },
        });
      }
      await apolloClient.refetchQueries({ include: 'active' });
      onSaved?.();
      handleClose();
    } catch (error) {
      setFormError(error instanceof Error ? error.message : 'Unable to save author.');
    }
  };

  const handleClose = () => {
    setName('');
    setFormError(null);
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth PaperProps={{ sx: { borderRadius: 2 } }}>
      <DialogTitle sx={{ pb: 1 }}>
        <Stack direction="row" spacing={1.5} alignItems="center">
          {isEditing ? <DriveFileRenameOutline color="primary" /> : <PersonAdd color="primary" />}
          <Box>
            <Typography variant="h6">
              {isEditing ? 'Rename Author' : 'Create Author'}
            </Typography>
            {isEditing && (
              <Typography variant="body2" color="text.secondary">
                Version {author.version}
              </Typography>
            )}
          </Box>
        </Stack>
      </DialogTitle>
      <form onSubmit={handleSubmit}>
        <DialogContent sx={{ pt: 2 }}>
          <Box sx={{ pt: 1 }}>
            {formError && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {formError}
              </Alert>
            )}
            <TextField
              autoFocus
              margin="dense"
              label="Author name"
              fullWidth
              variant="outlined"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button onClick={handleClose} disabled={saving}>Cancel</Button>
          <Button type="submit" variant="contained" disabled={saving}>
            {saving ? 'Saving...' : isEditing ? 'Save Name' : 'Create Author'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

export default AuthorForm;
