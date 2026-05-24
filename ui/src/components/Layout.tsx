import React from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Container,
  Box,
  Button,
  Stack,
} from '@mui/material';
import { alpha } from '@mui/material/styles';
import { Link, useLocation } from 'react-router-dom';
import { AutoStories, Dashboard, MenuBook, PeopleAlt } from '@mui/icons-material';

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const location = useLocation();
  const navItems = [
    { label: 'Workspace', to: '/', icon: <Dashboard fontSize="small" /> },
    { label: 'Authors', to: '/authors', icon: <PeopleAlt fontSize="small" /> },
    { label: 'Books', to: '/books', icon: <MenuBook fontSize="small" /> },
  ];

  return (
    <Box sx={{ minHeight: '100vh' }}>
      <AppBar
        position="sticky"
        elevation={0}
        sx={{
          background: 'rgba(255, 255, 255, 0.92)',
          color: 'text.primary',
          borderBottom: '1px solid',
          borderColor: 'divider',
        }}
      >
        <Toolbar sx={{ gap: 2, minHeight: 72 }}>
          <Stack direction="row" spacing={1.5} alignItems="center" sx={{ flexGrow: 1, minWidth: 0 }}>
            <Box
              sx={{
                width: 42,
                height: 42,
                display: 'grid',
                placeItems: 'center',
                color: 'common.white',
                bgcolor: 'primary.dark',
                borderRadius: 2,
                boxShadow: `0 12px 28px ${alpha('#0f766e', 0.26)}`,
              }}
            >
              <AutoStories />
            </Box>
            <Box sx={{ minWidth: 0 }}>
              <Typography variant="h6" component="div" noWrap>
                Catalog Console
              </Typography>
              <Typography variant="caption" color="text.secondary" noWrap>
                Spring GraphQL
              </Typography>
            </Box>
          </Stack>
          <Stack direction="row" spacing={1} sx={{ display: { xs: 'none', sm: 'flex' } }}>
            {navItems.map((item) => {
              const active = location.pathname === item.to;
              return (
                <Button
                  key={item.to}
                  color={active ? 'primary' : 'inherit'}
                  component={Link}
                  to={item.to}
                  startIcon={item.icon}
                  variant={active ? 'contained' : 'text'}
                  sx={{
                    color: active ? 'common.white' : 'text.secondary',
                    bgcolor: active ? 'primary.main' : 'transparent',
                    '&:hover': {
                      bgcolor: active ? 'primary.dark' : alpha('#0f766e', 0.08),
                    },
                  }}
                >
                  {item.label}
                </Button>
              );
            })}
          </Stack>
        </Toolbar>
      </AppBar>
      <Container maxWidth="xl" sx={{ py: { xs: 3, md: 4 } }}>
        {children}
      </Container>
    </Box>
  );
};

export default Layout;
