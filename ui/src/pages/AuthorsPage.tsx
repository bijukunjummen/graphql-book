import React from 'react';
import { Stack, Typography } from '@mui/material';
import AuthorList from '../components/AuthorList';

const AuthorsPage: React.FC = () => {
  return (
    <Stack spacing={3}>
      <Typography variant="h4" component="h1">
        Authors
      </Typography>
      <AuthorList title="Author Directory" />
    </Stack>
  );
};

export default AuthorsPage;
