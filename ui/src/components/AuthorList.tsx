import React, { useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Chip,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
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
  Tooltip,
  Typography,
} from '@mui/material';
import { alpha } from '@mui/material/styles';
import type { SelectChangeEvent } from '@mui/material/Select';
import { Add, ChevronLeft, ChevronRight, DriveFileRenameOutline, PeopleAlt, Refresh } from '@mui/icons-material';
import { useFindAuthors } from '../hooks/useGraphQL';
import type { Author, SortInput } from '../types/graphql';
import AuthorForm from './AuthorForm';

const PAGE_SIZE = 10;

interface SortOption {
  label: string;
  value: string;
  sort: SortInput[];
}

const DEFAULT_AUTHOR_SORT: SortOption = {
  label: 'Name A-Z',
  value: 'name-asc',
  sort: [{ field: 'name', order: 'ASC' }],
};

const AUTHOR_SORT_OPTIONS: SortOption[] = [
  DEFAULT_AUTHOR_SORT,
  { label: 'Name Z-A', value: 'name-desc', sort: [{ field: 'name', order: 'DESC' }] },
  { label: 'Version low-high', value: 'version-asc', sort: [{ field: 'version', order: 'ASC' }] },
  { label: 'Version high-low', value: 'version-desc', sort: [{ field: 'version', order: 'DESC' }] },
];

interface AuthorListProps {
  title?: string;
  showCreateAction?: boolean;
}

const AuthorList: React.FC<AuthorListProps> = ({ title = 'Authors', showCreateAction = true }) => {
  const [formOpen, setFormOpen] = useState(false);
  const [editingAuthor, setEditingAuthor] = useState<Author | null>(null);
  const [sortValue, setSortValue] = useState(DEFAULT_AUTHOR_SORT.value);
  const [pageIndex, setPageIndex] = useState(0);
  const [afterCursors, setAfterCursors] = useState<(string | undefined)[]>([undefined]);

  const selectedSort = AUTHOR_SORT_OPTIONS.find((option) => option.value === sortValue) ?? DEFAULT_AUTHOR_SORT;
  const afterCursor = afterCursors[pageIndex];
  const { data, loading, error, refetch } = useFindAuthors(PAGE_SIZE, afterCursor, selectedSort.sort);

  const authors = (data?.findAuthors?.edges ?? [])
    .map((edge) => edge?.node)
    .filter((author): author is Author => Boolean(author));
  const totalCount = data?.findAuthors?.totalCount ?? authors.length;
  const pageInfo = data?.findAuthors?.pageInfo;
  const totalPages = Math.max(1, Math.ceil(totalCount / PAGE_SIZE));
  const rangeStart = totalCount === 0 ? 0 : pageIndex * PAGE_SIZE + 1;
  const rangeEnd = Math.min(pageIndex * PAGE_SIZE + authors.length, totalCount);
  const canGoPrevious = pageIndex > 0;
  const canGoNext = Boolean(pageInfo?.hasNextPage && pageInfo.endCursor);

  const openCreate = () => {
    setEditingAuthor(null);
    setFormOpen(true);
  };

  const openEdit = (author: Author) => {
    setEditingAuthor(author);
    setFormOpen(true);
  };

  const closeForm = () => {
    setFormOpen(false);
    setEditingAuthor(null);
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
              bgcolor: alpha('#0f766e', 0.12),
              color: 'primary.dark',
            }}
          >
            <PeopleAlt />
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
            <InputLabel id="author-sort-label">Sort</InputLabel>
            <Select
              labelId="author-sort-label"
              label="Sort"
              value={sortValue}
              onChange={changeSort}
            >
              {AUTHOR_SORT_OPTIONS.map((option) => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <Tooltip title="Refresh authors">
            <IconButton aria-label="Refresh authors" onClick={refresh}>
              <Refresh />
            </IconButton>
          </Tooltip>
          {showCreateAction && (
            <Button variant="contained" startIcon={<Add />} onClick={openCreate} sx={{ width: { xs: '100%', sm: 'auto' } }}>
              Add Author
            </Button>
          )}
        </Stack>
      </Stack>

      {error && (
        <Alert severity="error" sx={{ m: 2.5 }}>
          {error.message}
        </Alert>
      )}

      <TableContainer component={Box} sx={{ overflowX: 'auto' }}>
        <Table sx={{ minWidth: 640 }}>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>ID</TableCell>
              <TableCell>Version</TableCell>
              <TableCell align="right">Mutation</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loading ? (
              Array.from({ length: PAGE_SIZE }).map((_, index) => (
                <TableRow key={index}>
                  <TableCell><Skeleton width="70%" /></TableCell>
                  <TableCell><Skeleton width="90%" /></TableCell>
                  <TableCell><Skeleton width={56} /></TableCell>
                  <TableCell align="right"><Skeleton width={42} sx={{ ml: 'auto' }} /></TableCell>
                </TableRow>
              ))
            ) : authors.length === 0 ? (
              <TableRow>
                <TableCell colSpan={4}>
                  <Box sx={{ py: 5, textAlign: 'center' }}>
                    <Typography variant="subtitle1">No authors yet</Typography>
                  </Box>
                </TableCell>
              </TableRow>
            ) : (
              authors.map((author) => (
                <TableRow key={author.id} hover>
                  <TableCell>
                    <Typography variant="body1" fontWeight={700}>
                      {author.name}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Typography
                      variant="caption"
                      sx={{ fontFamily: 'ui-monospace, SFMono-Regular, Menlo, monospace', color: 'text.secondary' }}
                    >
                      {author.id}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Chip label={`v${author.version}`} size="small" variant="outlined" color="primary" />
                  </TableCell>
                  <TableCell align="right">
                    <Tooltip title="Rename author">
                      <IconButton
                        aria-label={`Rename ${author.name}`}
                        color="primary"
                        onClick={() => openEdit(author)}
                      >
                        <DriveFileRenameOutline />
                      </IconButton>
                    </Tooltip>
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

      <AuthorForm
        open={formOpen}
        onClose={closeForm}
        onSaved={refresh}
        author={editingAuthor}
      />
    </Paper>
  );
};

export default AuthorList;
