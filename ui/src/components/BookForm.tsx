import React, { useState, useEffect } from 'react';
import {
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Box,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  OutlinedInput,
  Stack,
  Typography,
} from '@mui/material';
import { useApolloClient } from '@apollo/client/react';
import { AddBox, DriveFileRenameOutline } from '@mui/icons-material';
import { useCreateBook, useUpdateBookName, useFindAuthors } from '../hooks/useGraphQL';
import type { Author } from '../types/graphql';

interface BookFormProps {
  open: boolean;
  onClose: () => void;
  onSaved?: () => void;
  book?: {
    id: string;
    name: string;
    pageCount?: number | null;
    version: number;
    authors?: Array<{ id: string; name: string }> | null;
  } | null;
}

const BookForm: React.FC<BookFormProps> = ({ open, onClose, onSaved, book }) => {
  const [name, setName] = useState(book?.name || '');
  const [pageCount, setPageCount] = useState(book?.pageCount?.toString() || '');
  const [selectedAuthors, setSelectedAuthors] = useState<string[]>(
    book?.authors?.map(author => author.id) || []
  );
  const [formError, setFormError] = useState<string | null>(null);

  const [createBook, { loading: creating }] = useCreateBook();
  const [updateBookName, { loading: updating }] = useUpdateBookName();
  const { data: authorsData } = useFindAuthors(100);
  const apolloClient = useApolloClient();

  const isEditing = !!book;
  const saving = creating || updating;

  useEffect(() => {
    if (book) {
      setName(book.name || '');
      setPageCount(book.pageCount?.toString() || '');
      setSelectedAuthors(book.authors?.map(author => author.id) || []);
    } else {
      setName('');
      setPageCount('');
      setSelectedAuthors([]);
    }
    setFormError(null);
  }, [book]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const trimmedName = name.trim();
    if (!trimmedName) {
      setFormError('Book name is required.');
      return;
    }
    if (!isEditing && selectedAuthors.length === 0) {
      setFormError('Select at least one author.');
      return;
    }
    
    try {
      if (isEditing) {
        await updateBookName({
          variables: {
            input: {
              id: book.id,
              name: trimmedName,
              version: book.version,
            },
          },
        });
      } else {
        await createBook({
          variables: {
            input: {
              name: trimmedName,
              pageCount: pageCount ? parseInt(pageCount) : undefined,
              authors: selectedAuthors,
            },
          },
        });
      }
      await apolloClient.refetchQueries({ include: 'active' });
      onSaved?.();
      handleClose();
    } catch (error) {
      setFormError(error instanceof Error ? error.message : 'Unable to save book.');
    }
  };

  const handleClose = () => {
    setName('');
    setPageCount('');
    setSelectedAuthors([]);
    setFormError(null);
    onClose();
  };

  const authors = (authorsData?.findAuthors?.edges ?? [])
    .map((edge) => edge?.node)
    .filter((author): author is Author => Boolean(author));

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth PaperProps={{ sx: { borderRadius: 2 } }}>
      <DialogTitle sx={{ pb: 1 }}>
        <Stack direction="row" spacing={1.5} alignItems="center">
          {isEditing ? <DriveFileRenameOutline color="secondary" /> : <AddBox color="secondary" />}
          <Box>
            <Typography variant="h6">
              {isEditing ? 'Rename Book' : 'Create Book'}
            </Typography>
            {isEditing && (
              <Typography variant="body2" color="text.secondary">
                Version {book.version}
              </Typography>
            )}
          </Box>
        </Stack>
      </DialogTitle>
      <form onSubmit={handleSubmit}>
        <DialogContent sx={{ pt: 2 }}>
          <Box sx={{ pt: 1, display: 'flex', flexDirection: 'column', gap: 2 }}>
            {formError && (
              <Alert severity="error">
                {formError}
              </Alert>
            )}
            <TextField
              autoFocus
              margin="dense"
              label="Book name"
              fullWidth
              variant="outlined"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
            
            {!isEditing && (
              <>
                <TextField
                  margin="dense"
                  label="Page Count"
                  type="number"
                  fullWidth
                  variant="outlined"
                  value={pageCount}
                  onChange={(e) => setPageCount(e.target.value)}
                  inputProps={{ min: 0 }}
                />

                <FormControl fullWidth margin="dense">
                  <InputLabel>Authors</InputLabel>
                  <Select<string[]>
                    multiple
                    value={selectedAuthors}
                    onChange={(e) => {
                      const value = e.target.value;
                      setSelectedAuthors(typeof value === 'string' ? value.split(',') : value);
                    }}
                    input={<OutlinedInput label="Authors" />}
                    renderValue={(selected) => (
                      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                        {selected.map((value) => {
                          const author = authors.find((candidate) => candidate.id === value);
                          return (
                            <Chip key={value} label={author?.name} size="small" />
                          );
                        })}
                      </Box>
                    )}
                    required
                  >
                    {authors.map((author) => (
                      <MenuItem key={author.id} value={author.id}>
                        {author.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </>
            )}
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button onClick={handleClose} disabled={saving}>Cancel</Button>
          <Button type="submit" variant="contained" disabled={saving} color="secondary">
            {saving ? 'Saving...' : isEditing ? 'Save Name' : 'Create Book'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

export default BookForm;
