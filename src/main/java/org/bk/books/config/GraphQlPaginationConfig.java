package org.bk.books.config;

import graphql.relay.Edge;
import graphql.relay.PageInfo;
import org.bk.books.web.pagination.PageConnectionAdapter;
import org.springframework.boot.graphql.autoconfigure.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.graphql.data.pagination.ConnectionAdapter;
import org.springframework.graphql.data.pagination.ConnectionFieldTypeVisitor;
import org.springframework.graphql.data.pagination.CursorStrategy;
import org.springframework.graphql.data.query.SliceConnectionAdapter;
import org.springframework.graphql.data.query.WindowConnectionAdapter;

import java.util.Collection;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class GraphQlPaginationConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @SuppressWarnings("unchecked")
    GraphQlSourceBuilderCustomizer pageConnectionAdapterCustomizer(CursorStrategy<?> cursorStrategy) {
        if (!cursorStrategy.supports(ScrollPosition.class)) {
            return builder -> {
            };
        }

        CursorStrategy<ScrollPosition> scrollCursorStrategy = (CursorStrategy<ScrollPosition>) cursorStrategy;
        ConnectionFieldTypeVisitor visitor = ConnectionFieldTypeVisitor.create(
            new DelegatingConnectionAdapter(List.of(
                new PageConnectionAdapter(scrollCursorStrategy),
                new WindowConnectionAdapter(scrollCursorStrategy),
                new SliceConnectionAdapter(scrollCursorStrategy)
            ))
        );
        return builder -> builder.typeVisitors(List.of(visitor));
    }

    private record DelegatingConnectionAdapter(List<ConnectionAdapter> adapters) implements ConnectionAdapter {
        @Override
        public boolean supports(Class<?> containerType) {
            return adapters.stream().anyMatch(adapter -> adapter.supports(containerType));
        }

        @Override
        public <T> Collection<T> getContent(Object container) {
            return adapter(container).getContent(container);
        }

        @Override
        public boolean hasPrevious(Object container) {
            return adapter(container).hasPrevious(container);
        }

        @Override
        public boolean hasNext(Object container) {
            return adapter(container).hasNext(container);
        }

        @Override
        public String cursorAt(Object container, int index) {
            return adapter(container).cursorAt(container, index);
        }

        @Override
        public <T> Object createConnection(Object container, List<Edge<T>> edges, PageInfo pageInfo) {
            return adapter(container).createConnection(container, edges, pageInfo);
        }

        private ConnectionAdapter adapter(Object container) {
            return adapters.stream()
                .filter(adapter -> adapter.supports(container.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No ConnectionAdapter for: " + container.getClass().getName()));
        }
    }
}
