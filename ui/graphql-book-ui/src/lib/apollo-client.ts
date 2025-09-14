import { ApolloClient, InMemoryCache } from '@apollo/client';
import { createHttpLink } from '@apollo/client/link/http';
import { RetryLink } from '@apollo/client/link/retry';
import { from } from '@apollo/client/core';

const httpLink = createHttpLink({
  uri: '/graphql',
});

const retryLink = new RetryLink({
  delay: {
    initial: 300,
    max: 3000,
    jitter: true,
  },
  attempts: {
    max: 3,
    retryIf: (error: any, _operation) => {
      if (error.networkError && typeof error.networkError === 'object') {
        // Retry on network errors or 429 (Too Many Requests)
        return error.networkError.statusCode === 429 || !error.networkError.statusCode;
      }
      return false;
    },
  },
});

export const client = new ApolloClient({
  link: from([retryLink, httpLink]),
  cache: new InMemoryCache({
    typePolicies: {
      Query: {
        fields: {
          findBooks: {
            keyArgs: false,
            merge(existing, incoming) {
              if (!existing) return incoming;
              if (!incoming) return existing;

              const { edges: existingEdges, pageInfo: existingPageInfo, ...rest } = existing;
              const { edges: incomingEdges, pageInfo: incomingPageInfo } = incoming;

              return {
                ...rest,
                edges: [...existingEdges, ...incomingEdges],
                pageInfo: incomingPageInfo,
              };
            },
          },
        },
      },
    },
  }),
  defaultOptions: {
    watchQuery: {
      fetchPolicy: 'cache-and-network',
      errorPolicy: 'all',
    },
    query: {
      fetchPolicy: 'network-only',
      errorPolicy: 'all',
    },
    mutate: {
      errorPolicy: 'all',
    },
  },
});
