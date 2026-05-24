import React, { useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Chip,
  Paper,
  Snackbar,
  Stack,
  Typography,
} from '@mui/material';
import { alpha } from '@mui/material/styles';
import { useApolloClient } from '@apollo/client/react';
import { AutoStories, Bolt, DataObject, PeopleAlt, Refresh } from '@mui/icons-material';
import AuthorList from '../components/AuthorList';
import BookList from '../components/BookList';
import { useFindAuthors, useFindBooks, useLoadSampleData } from '../hooks/useGraphQL';

const HomePage: React.FC = () => {
  const { data: authorData } = useFindAuthors(10);
  const { data: bookData } = useFindBooks(10);
  const [loadSampleData, { loading: loadingSampleData }] = useLoadSampleData();
  const [notice, setNotice] = useState<string | null>(null);
  const apolloClient = useApolloClient();

  const authorCount = authorData?.findAuthors?.totalCount ?? 0;
  const bookCount = bookData?.findBooks?.totalCount ?? 0;

  const refresh = async () => {
    await apolloClient.refetchQueries({ include: 'active' });
  };

  const loadData = async () => {
    const response = await loadSampleData();
    await refresh();
    const result = response.data?.loadSampleData;
    setNotice(result ? `Loaded ${result.booksLoaded} books and ${result.authorsLoaded} authors.` : 'Sample data loaded.');
  };

  return (
    <Stack spacing={3}>
      <Paper
        elevation={0}
        sx={{
          p: { xs: 2.5, md: 3 },
          bgcolor: '#0f172a',
          color: 'common.white',
          overflow: 'hidden',
          border: '1px solid',
          borderColor: alpha('#ffffff', 0.12),
        }}
      >
        <Stack
          direction={{ xs: 'column', lg: 'row' }}
          spacing={3}
          alignItems={{ xs: 'stretch', lg: 'center' }}
          justifyContent="space-between"
        >
          <Stack spacing={1.5}>
            <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
              <Chip
                icon={<Bolt />}
                label="GraphQL mutations"
                size="small"
                sx={{ bgcolor: alpha('#14b8a6', 0.18), color: '#ccfbf1' }}
              />
              <Chip
                icon={<DataObject />}
                label="/graphql"
                size="small"
                sx={{ bgcolor: alpha('#f59e0b', 0.18), color: '#fef3c7' }}
              />
            </Stack>
            <Box>
              <Typography variant="h3" component="h1">
                Catalog Workspace
              </Typography>
              <Typography variant="body1" sx={{ color: alpha('#ffffff', 0.72), maxWidth: 720 }}>
                Spring GraphQL catalog
              </Typography>
            </Box>
          </Stack>

          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.25}>
            <Button
              variant="contained"
              color="warning"
              startIcon={<Bolt />}
              disabled={loadingSampleData}
              onClick={loadData}
            >
              {loadingSampleData ? 'Loading...' : 'Load Sample Data'}
            </Button>
            <Button
              variant="outlined"
              startIcon={<Refresh />}
              onClick={() => {
                void refresh();
              }}
              sx={{
                color: 'common.white',
                borderColor: alpha('#ffffff', 0.36),
                '&:hover': {
                  borderColor: alpha('#ffffff', 0.72),
                  bgcolor: alpha('#ffffff', 0.08),
                },
              }}
            >
              Refresh
            </Button>
          </Stack>
        </Stack>
      </Paper>

      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, minmax(0, 1fr))' },
          gap: 2,
        }}
      >
        <MetricTile icon={<AutoStories />} label="Books" value={bookCount} accent="#e11d48" />
        <MetricTile icon={<PeopleAlt />} label="Authors" value={authorCount} accent="#0f766e" />
      </Box>

      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: { xs: '1fr', lg: 'minmax(0, 1.2fr) minmax(0, 1fr)' },
          gap: 3,
          alignItems: 'start',
        }}
      >
        <BookList />
        <AuthorList />
      </Box>

      <Snackbar
        open={Boolean(notice)}
        autoHideDuration={3600}
        onClose={() => setNotice(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert onClose={() => setNotice(null)} severity="success" variant="filled">
          {notice}
        </Alert>
      </Snackbar>
    </Stack>
  );
};

interface MetricTileProps {
  icon: React.ReactNode;
  label: string;
  value: number;
  accent: string;
}

const MetricTile: React.FC<MetricTileProps> = ({ icon, label, value, accent }) => (
  <Paper
    elevation={0}
    sx={{
      p: 2.25,
      border: '1px solid',
      borderColor: 'divider',
      boxShadow: `0 16px 36px ${alpha('#0f172a', 0.07)}`,
    }}
  >
    <Stack direction="row" spacing={2} alignItems="center">
      <Box
        sx={{
          width: 48,
          height: 48,
          display: 'grid',
          placeItems: 'center',
          borderRadius: 2,
          bgcolor: alpha(accent, 0.12),
          color: accent,
        }}
      >
        {icon}
      </Box>
      <Box>
        <Typography variant="body2" color="text.secondary">
          {label}
        </Typography>
        <Typography variant="h4">{value}</Typography>
      </Box>
    </Stack>
  </Paper>
);

export default HomePage;
