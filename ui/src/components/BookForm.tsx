import React, { useState, useEffect } from 'react';
import {
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
} from '@mui/material';
import { useCreateBook, useUpdateBookName, useFindAuthors } from '../hooks/useGraphQL';

interface BookFormProps {
  open: boolean;
  onClose: () => void;
  book?: {
    id: string;
    name: string;
    pageCount?: number | null;
    version: number;
    authors?: Array<{ id: string; name: string }> | null;
  } | null;
}

const BookForm: React.FC<BookFormProps> = ({ open, onClose, book }) => {
  const [name, setName] = useState(book?.name || '');
  const [pageCount, setPageCount] = useState(book?.pageCount?.toString() || '');
  const [selectedAuthors, setSelectedAuthors] = useState<string[]>(
    book?.authors?.map(author => author.id) || []
  );

  const [createBook] = useCreateBook();
  const [updateBookName] = useUpdateBookName();
  const { data: authorsData } = useFindAuthors(100);

  const isEditing = !!book;

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
  }, [book]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    try {
      if (isEditing) {
        await updateBookName({
          variables: {
            input: {
              id: book.id,
              name: name,
              version: book.version,
            },
          },
        });
      } else {
        await createBook({
          variables: {
            input: {
              name: name,
              pageCount: pageCount ? parseInt(pageCount) : undefined,
              authors: selectedAuthors,
            },
          },
        });
      }
      
      handleClose();
    } catch (error) {
      console.error('Error saving book:', error);
    }
  };

  const handleClose = () => {
    setName('');
    setPageCount('');
    setSelectedAuthors([]);
    onClose();
  };

  const authors = authorsData?.findAuthors?.edges?.map((edge: any) => edge?.node).filter(Boolean) || [];

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        {isEditing ? 'Edit Book' : 'Add New Book'}
      </DialogTitle>
      <form onSubmit={handleSubmit}>
        <DialogContent>
          <Box sx={{ pt: 1, display: 'flex', flexDirection: 'column', gap: 2 }}>
            <TextField
              autoFocus
              margin="dense"
              label="Book Name"
              fullWidth
              variant="outlined"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
            
            <TextField
              margin="dense"
              label="Page Count"
              type="number"
              fullWidth
              variant="outlined"
              value={pageCount}
              onChange={(e) => setPageCount(e.target.value)}
            />

            {!isEditing && (
              <FormControl fullWidth margin="dense">
                <InputLabel>Authors</InputLabel>
                <Select
                  multiple
                  value={selectedAuthors}
                  onChange={(e) => setSelectedAuthors(e.target.value as string[])}
                  input={<OutlinedInput label="Authors" />}
                  renderValue={(selected) => (
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                      {selected.map((value) => {
                        const author = authors.find((a: any) => a?.id === value);
                        return (
                          <Chip key={value} label={author?.name} size="small" />
                        );
                      })}
                    </Box>
                  )}
                  required
                >
                  {authors.map((author: any) => (
                    <MenuItem key={author?.id} value={author?.id}>
                      {author?.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            )}
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

export default BookForm;
