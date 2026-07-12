import React, { useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Chip,
  FormControl,
  FormHelperText,
  IconButton,
  InputLabel,
  MenuItem,
  OutlinedInput,
  Paper,
  Select,
  Skeleton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Tooltip,
  Typography,
} from '@mui/material';
import { alpha } from '@mui/material/styles';
import type { SelectChangeEvent } from '@mui/material/Select';
import { Add, AutoStories, Check, ChevronLeft, ChevronRight, Close, DriveFileRenameOutline, Refresh } from '@mui/icons-material';
import { useFindAuthors, useFindBooks, useUpdateBookAuthors, useUpdateBookName } from '../hooks/useGraphQL';
import type { Author, Book, SortInput } from '../types/graphql';
import BookForm from './BookForm';

const PAGE_SIZE = 10;

interface SortOption {
  label: string;
  value: string;
  sort: SortInput[];
}

const DEFAULT_BOOK_SORT: SortOption = {
  label: 'Name A-Z',
  value: 'name-asc',
  sort: [{ field: 'name', order: 'ASC' }],
};

const BOOK_SORT_OPTIONS: SortOption[] = [
  DEFAULT_BOOK_SORT,
  { label: 'Name Z-A', value: 'name-desc', sort: [{ field: 'name', order: 'DESC' }] },
  { label: 'Pages low-high', value: 'pages-asc', sort: [{ field: 'pageCount', order: 'ASC' }] },
  { label: 'Pages high-low', value: 'pages-desc', sort: [{ field: 'pageCount', order: 'DESC' }] },
  { label: 'Version low-high', value: 'version-asc', sort: [{ field: 'version', order: 'ASC' }] },
  { label: 'Version high-low', value: 'version-desc', sort: [{ field: 'version', order: 'DESC' }] },
];

interface BookListProps {
  title?: string;
  showCreateAction?: boolean;
}

const BookList: React.FC<BookListProps> = ({ title = 'Books', showCreateAction = true }) => {
  const [formOpen, setFormOpen] = useState(false);
  const [inlineEditingId, setInlineEditingId] = useState<string | null>(null);
  const [draftName, setDraftName] = useState('');
  const [draftAuthorIds, setDraftAuthorIds] = useState<string[]>([]);
  const [inlineError, setInlineError] = useState<string | null>(null);
  const [sortValue, setSortValue] = useState(DEFAULT_BOOK_SORT.value);
  const [pageIndex, setPageIndex] = useState(0);
  const [afterCursors, setAfterCursors] = useState<(string | undefined)[]>([undefined]);

  const selectedSort = BOOK_SORT_OPTIONS.find((option) => option.value === sortValue) ?? DEFAULT_BOOK_SORT;
  const afterCursor = afterCursors[pageIndex];
  const { data, loading, error, refetch } = useFindBooks(PAGE_SIZE, afterCursor, selectedSort.sort);
  const { data: authorsData } = useFindAuthors(100);
  const [updateBookName, { loading: updatingName }] = useUpdateBookName();
  const [updateBookAuthors, { loading: updatingAuthors }] = useUpdateBookAuthors();

  const books = (data?.findBooks?.edges ?? [])
    .map((edge) => edge?.node)
    .filter((book): book is Book => Boolean(book));
  const totalCount = data?.findBooks?.totalCount ?? books.length;
  const pageInfo = data?.findBooks?.pageInfo;
  const totalPages = Math.max(1, Math.ceil(totalCount / PAGE_SIZE));
  const rangeStart = totalCount === 0 ? 0 : pageIndex * PAGE_SIZE + 1;
  const rangeEnd = Math.min(pageIndex * PAGE_SIZE + books.length, totalCount);
  const canGoPrevious = pageIndex > 0;
  const canGoNext = Boolean(pageInfo?.hasNextPage && pageInfo.endCursor);
  const inlineSaving = updatingName || updatingAuthors;
  const allAuthors = (authorsData?.findAuthors?.edges ?? [])
    .map((edge) => edge?.node)
    .filter((author): author is Author => Boolean(author));

  const openCreate = () => {
    setFormOpen(true);
  };

  const openInlineEdit = (book: Book) => {
    setInlineEditingId(book.id);
    setDraftName(book.name);
    setDraftAuthorIds(book.authors?.map((author) => author.id) ?? []);
    setInlineError(null);
  };

  const closeInlineEdit = () => {
    setInlineEditingId(null);
    setDraftName('');
    setDraftAuthorIds([]);
    setInlineError(null);
  };

  const closeForm = () => {
    setFormOpen(false);
  };

  const refresh = () => {
    void refetch();
  };

  const resetPaging = () => {
    setPageIndex(0);
    setAfterCursors([undefined]);
  };

  const changeSort = (event: SelectChangeEvent) => {
    setSortValue(event.target.value);
    resetPaging();
  };

  const goNext = () => {
    if (!pageInfo?.endCursor) {
      return;
    }
    setAfterCursors((current) => {
      const next = current.slice(0, pageIndex + 2);
      next[pageIndex + 1] = pageInfo.endCursor ?? undefined;
      return next;
    });
    setPageIndex((current) => current + 1);
  };

  const goPrevious = () => {
    setPageIndex((current) => Math.max(0, current - 1));
  };

  const saveInlineEdit = async (book: Book) => {
    const trimmedName = draftName.trim();
    if (!trimmedName) {
      setInlineError('Book name is required.');
      return;
    }

    const currentAuthorIds = (book.authors ?? []).map((author) => author.id);
    const nameChanged = trimmedName !== book.name;
    const authorsChanged =
      draftAuthorIds.length !== currentAuthorIds.length ||
      draftAuthorIds.some((id) => !currentAuthorIds.includes(id));

    if (!nameChanged && !authorsChanged) {
      closeInlineEdit();
      return;
    }

    try {
      let version = book.version;
      if (nameChanged) {
        const result = await updateBookName({
          variables: {
            input: {
              id: book.id,
              name: trimmedName,
              version,
            },
          },
        });
        version = result.data?.updateBookName?.book?.version ?? version + 1;
      }

      if (authorsChanged) {
        await updateBookAuthors({
          variables: {
            input: {
              id: book.id,
              authors: draftAuthorIds,
              version,
            },
          },
        });
      }

      await refetch();
      closeInlineEdit();
    } catch (saveError) {
      setInlineError(saveError instanceof Error ? saveError.message : 'Unable to update book.');
    }
  };

  return (
    <Paper
      elevation={0}
      sx={{
        overflow: 'hidden',
        border: '1px solid',
        borderColor: 'divider',
        boxShadow: `0 20px 45px ${alpha('#0f172a', 0.08)}`,
      }}
    >
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={2}
        alignItems={{ xs: 'stretch', sm: 'center' }}
        justifyContent="space-between"
        sx={{ p: 2.5, borderBottom: '1px solid', borderColor: 'divider' }}
      >
        <Stack direction="row" spacing={1.5} alignItems="center">
          <Box
            sx={{
              width: 38,
              height: 38,
              borderRadius: 2,
              display: 'grid',
              placeItems: 'center',
              bgcolor: alpha('#e11d48', 0.12),
              color: 'secondary.dark',
            }}
          >
            <AutoStories />
          </Box>
          <Box>
            <Typography variant="h6">{title}</Typography>
            <Typography variant="body2" color="text.secondary">
              {totalCount} total
            </Typography>
          </Box>
        </Stack>
        <Stack
          direction={{ xs: 'column', sm: 'row' }}
          spacing={1}
          alignItems={{ xs: 'stretch', sm: 'center' }}
          justifyContent={{ xs: 'flex-start', sm: 'flex-end' }}
        >
          <FormControl size="small" sx={{ minWidth: { xs: '100%', sm: 176 } }}>
            <InputLabel id="book-sort-label">Sort</InputLabel>
            <Select
              labelId="book-sort-label"
              label="Sort"
              value={sortValue}
              onChange={changeSort}
            >
              {BOOK_SORT_OPTIONS.map((option) => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <Tooltip title="Refresh books">
            <IconButton aria-label="Refresh books" onClick={refresh}>
              <Refresh />
            </IconButton>
          </Tooltip>
          {showCreateAction && (
            <Button variant="contained" color="secondary" startIcon={<Add />} onClick={openCreate} sx={{ width: { xs: '100%', sm: 'auto' } }}>
              Add Book
            </Button>
          )}
        </Stack>
      </Stack>

      {error && (
        <Alert severity="error" sx={{ m: 2.5 }}>
          {error.message}
        </Alert>
      )}
      {inlineError && (
        <Alert severity="error" sx={{ m: 2.5, mt: error ? 0 : 2.5 }}>
          {inlineError}
        </Alert>
      )}

      <TableContainer component={Box} sx={{ overflowX: 'auto' }}>
        <Table sx={{ minWidth: 720 }}>
          <TableHead>
            <TableRow>
              <TableCell>Book</TableCell>
              <TableCell>Authors</TableCell>
              <TableCell>Pages</TableCell>
              <TableCell>Version</TableCell>
              <TableCell align="right">Mutation</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loading ? (
              Array.from({ length: PAGE_SIZE }).map((_, index) => (
                <TableRow key={index}>
                  <TableCell><Skeleton width="78%" /></TableCell>
                  <TableCell><Skeleton width="90%" /></TableCell>
                  <TableCell><Skeleton width={48} /></TableCell>
                  <TableCell><Skeleton width={56} /></TableCell>
                  <TableCell align="right"><Skeleton width={42} sx={{ ml: 'auto' }} /></TableCell>
                </TableRow>
              ))
            ) : books.length === 0 ? (
              <TableRow>
                <TableCell colSpan={5}>
                  <Box sx={{ py: 5, textAlign: 'center' }}>
                    <Typography variant="subtitle1">No books yet</Typography>
                  </Box>
                </TableCell>
              </TableRow>
            ) : (
              books.map((book) => (
                <TableRow key={book.id} hover>
                  <TableCell>
                    {inlineEditingId === book.id ? (
                      <TextField
                        size="small"
                        fullWidth
                        value={draftName}
                        onChange={(event) => setDraftName(event.target.value)}
                        disabled={inlineSaving}
                        autoFocus
                      />
                    ) : (
                      <Typography variant="body1" fontWeight={800}>
                        {book.name}
                      </Typography>
                    )}
                    <Typography
                      variant="caption"
                      sx={{ fontFamily: 'ui-monospace, SFMono-Regular, Menlo, monospace', color: 'text.secondary' }}
                    >
                      {book.id}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    {inlineEditingId === book.id ? (
                      <FormControl fullWidth size="small">
                        <InputLabel>Authors</InputLabel>
                        <Select<string[]>
                          multiple
                          value={draftAuthorIds}
                          disabled={inlineSaving}
                          onChange={(event) => {
                            const value = event.target.value;
                            setDraftAuthorIds(typeof value === 'string' ? value.split(',') : value);
                          }}
                          input={<OutlinedInput label="Authors" />}
                          renderValue={(selected) => (
                            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                              {selected.map((value) => {
                                const author = allAuthors.find((candidate) => candidate.id === value);
                                return <Chip key={value} label={author?.name ?? value} size="small" />;
                              })}
                            </Box>
                          )}
                        >
                          {allAuthors.map((author) => (
                            <MenuItem key={author.id} value={author.id}>
                              {author.name}
                            </MenuItem>
                          ))}
                        </Select>
                        <FormHelperText>Inline editable</FormHelperText>
                      </FormControl>
                    ) : (
                      <Stack direction="row" spacing={0.75} useFlexGap flexWrap="wrap">
                        {book.authors?.length ? (
                          book.authors.map((author) => (
                            <Chip
                              key={author.id}
                              label={author.name}
                              size="small"
                              sx={{ bgcolor: alpha('#0f766e', 0.1), color: 'primary.dark' }}
                            />
                          ))
                        ) : (
                          <Typography variant="body2" color="text.secondary">
                            None
                          </Typography>
                        )}
                      </Stack>
                    )}
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2" color="text.secondary">
                      {book.pageCount ?? 'N/A'}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Chip label={`v${book.version}`} size="small" variant="outlined" color="secondary" />
                  </TableCell>
                  <TableCell align="right">
                    {inlineEditingId === book.id ? (
                      <Stack direction="row" spacing={0.5} justifyContent="flex-end">
                        <Tooltip title="Save changes">
                          <span>
                            <IconButton
                              aria-label={`Save ${book.name}`}
                              color="secondary"
                              onClick={() => void saveInlineEdit(book)}
                              disabled={inlineSaving}
                            >
                              <Check />
                            </IconButton>
                          </span>
                        </Tooltip>
                        <Tooltip title="Cancel edit">
                          <span>
                            <IconButton
                              aria-label={`Cancel edit ${book.name}`}
                              onClick={closeInlineEdit}
                              disabled={inlineSaving}
                            >
                              <Close />
                            </IconButton>
                          </span>
                        </Tooltip>
                      </Stack>
                    ) : (
                      <Tooltip title="Edit inline">
                        <IconButton
                          aria-label={`Edit ${book.name}`}
                          color="secondary"
                          onClick={() => openInlineEdit(book)}
                          disabled={Boolean(inlineEditingId)}
                        >
                          <DriveFileRenameOutline />
                        </IconButton>
                      </Tooltip>
                    )}
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={1.5}
        alignItems={{ xs: 'stretch', sm: 'center' }}
        justifyContent="space-between"
        sx={{ p: 2, borderTop: '1px solid', borderColor: 'divider' }}
      >
        <Typography variant="body2" color="text.secondary">
          Showing {rangeStart}-{rangeEnd} of {totalCount}
        </Typography>
        <Stack direction="row" spacing={1} alignItems="center" justifyContent={{ xs: 'space-between', sm: 'flex-end' }}>
          <Button
            size="small"
            variant="outlined"
            startIcon={<ChevronLeft />}
            disabled={!canGoPrevious || loading}
            onClick={goPrevious}
          >
            Previous
          </Button>
          <Chip label={`Page ${pageIndex + 1} of ${totalPages}`} size="small" />
          <Button
            size="small"
            variant="outlined"
            endIcon={<ChevronRight />}
            disabled={!canGoNext || loading}
            onClick={goNext}
          >
            Next
          </Button>
        </Stack>
      </Stack>

      <BookForm
        open={formOpen}
        onClose={closeForm}
        onSaved={refresh}
        book={null}
      />
    </Paper>
  );
};

export default BookList;
