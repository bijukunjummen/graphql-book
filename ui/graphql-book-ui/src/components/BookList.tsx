import { useState } from 'react';
import { useQuery } from '@apollo/client/react';
import { GET_BOOKS } from '../graphql/queries';
import {
  Box,
  Typography,
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  TablePagination,
  Chip,
  TableSortLabel,
  Snackbar,
  Alert,
} from '@mui/material';

interface Author {
  id: string;
  name: string;
}

type SortField = 'name' | 'pageCount';
type SortOrder = 'ASC' | 'DESC';

interface Sort {
  field: SortField;
  order: SortOrder;
}

interface Book {
  id: string;
  name: string;
  pageCount: number;
  authors: Author[];
}

interface BookEdge {
  node: Book;
}

interface PageInfo {
  hasNextPage: boolean;
  hasPreviousPage: boolean;
  startCursor: string;
  endCursor: string;
}

interface BooksData {
  findBooks: {
    edges: BookEdge[];
    pageInfo: PageInfo;
    totalCount: number;
  };
}

export const BookList = () => {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [sort, setSort] = useState<Sort>({ field: 'name', order: 'ASC' });
  const [isSorting, setIsSorting] = useState(false);
  const [isChangingPageSize, setIsChangingPageSize] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const { loading, error, data, fetchMore, refetch } = useQuery<BooksData>(GET_BOOKS, {
    variables: { first: rowsPerPage, sort: [{ field: sort.field, order: sort.order }] },
    notifyOnNetworkStatusChange: true,
  });

  const handleSort = async (field: SortField) => {
    try {
      setIsSorting(true);
      const newOrder = sort.field === field && sort.order === 'ASC' ? 'DESC' : 'ASC';
      setSort({ field, order: newOrder });
      setPage(0);
      await refetch({
        first: rowsPerPage,
        sort: [{ field, order: newOrder }]
      });
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Unable to sort books. Please try again.';
      setErrorMessage(message);
    } finally {
      setIsSorting(false);
    }
  };

  const handleChangePage = async (_: unknown, newPage: number) => {
    try {
      setPage(newPage);
      if (data?.findBooks?.pageInfo?.hasNextPage) {
        await fetchMore({
          variables: {
            after: data.findBooks.pageInfo.endCursor,
            first: rowsPerPage,
            sort: [{ field: sort.field, order: sort.order }]
          },
          updateQuery: (prev, { fetchMoreResult }) => {
            if (!fetchMoreResult) return prev;
            return fetchMoreResult;
          },
        });
      }
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Unable to load more books. Please try again.';
      setErrorMessage(message);
      setPage(Math.max(0, newPage - 1));
    }
  };

  const handleChangeRowsPerPage = async (event: React.ChangeEvent<HTMLInputElement>) => {
    try {
      setIsChangingPageSize(true);
      const newRowsPerPage = parseInt(event.target.value, 10);
      setRowsPerPage(newRowsPerPage);
      setPage(0);
      await refetch({
        first: newRowsPerPage,
        sort: [{ field: sort.field, order: sort.order }]
      });
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Unable to change page size. Please try again.';
      setErrorMessage(message);
    } finally {
      setIsChangingPageSize(false);
    }
  };

  const isLoading = loading || isSorting || isChangingPageSize || data?.findBooks?.edges?.length === 0;
  
  if (isLoading) {
    return (
      <Box sx={{ p: 3, display: 'flex', justifyContent: 'center' }}>
        <CircularProgress />
      </Box>
    );
  }
  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography color="error" gutterBottom>Error loading books</Typography>
        <Typography color="text.secondary">{error.message}</Typography>
      </Box>
    );
  }

  if (!data?.findBooks?.edges?.length) {
    return (
      <Box sx={{ p: 3, textAlign: 'center' }}>
        <Typography color="text.secondary" gutterBottom>No books found</Typography>
        <Typography variant="body2">Add a new book using the form above</Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Snackbar
        open={!!errorMessage}
        autoHideDuration={6000}
        onClose={() => setErrorMessage(null)}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert
          onClose={() => setErrorMessage(null)}
          severity="error"
          variant="filled"
          sx={{ width: '100%' }}
        >
          {errorMessage}
        </Alert>
      </Snackbar>
      <Typography variant="h4" gutterBottom>Books</Typography>
      <TableContainer component={Paper} sx={{ position: 'relative' }}>
        {isSorting && (
          <Box
            sx={{
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              backgroundColor: 'rgba(255, 255, 255, 0.7)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              zIndex: 1,
            }}
          >
            <CircularProgress />
          </Box>
        )}
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>
                <TableSortLabel
                  active={sort.field === 'name'}
                  direction={sort.field === 'name' ? sort.order.toLowerCase() as 'asc' | 'desc' : 'asc'}
                  onClick={() => handleSort('name')}
                  disabled={isSorting}
                >
                  Name
                </TableSortLabel>
              </TableCell>
              <TableCell align="right">
                <TableSortLabel
                  active={sort.field === 'pageCount'}
                  direction={sort.field === 'pageCount' ? sort.order.toLowerCase() as 'asc' | 'desc' : 'asc'}
                  onClick={() => handleSort('pageCount')}
                  disabled={isSorting}
                >
                  Page Count
                </TableSortLabel>
              </TableCell>
              <TableCell>Authors</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {data?.findBooks?.edges?.map((edge) => {
              if (!edge?.node) return null;
              const book = edge.node;
              return (
                <TableRow key={book.id}>
                  <TableCell component="th" scope="row">
                    {book.name}
                  </TableCell>
                  <TableCell align="right">{book.pageCount}</TableCell>
                  <TableCell>
                    <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                      {book.authors?.filter((author): author is Author => author !== null)
                        .map((author) => (
                          <Chip
                            key={author.id}
                            label={author.name}
                            size="small"
                            variant="outlined"
                          />
                        ))}
                    </Box>
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        component="div"
        count={data?.findBooks?.totalCount || 0}
        page={page}
        onPageChange={handleChangePage}
        rowsPerPage={rowsPerPage}
        onRowsPerPageChange={handleChangeRowsPerPage}
        rowsPerPageOptions={[5, 10, 25]}
      />
    </Box>
  );
};
