package org.bk.books.web.pagination;

import graphql.relay.Edge;
import graphql.relay.PageInfo;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.graphql.data.pagination.ConnectionAdapter;
import org.springframework.graphql.data.pagination.ConnectionAdapterSupport;
import org.springframework.graphql.data.pagination.CursorStrategy;

public class PageConnectionAdapter extends ConnectionAdapterSupport<ScrollPosition> implements ConnectionAdapter {
    public PageConnectionAdapter(CursorStrategy<ScrollPosition> cursorStrategy) {
        super(cursorStrategy);
    }

    @Override
    public boolean supports(Class<?> containerType) {
        return Page.class.isAssignableFrom(containerType);
    }

    @Override
    public <T> Collection<T> getContent(Object container) {
        Page<T> page = page(container);
        return page.getContent();
    }

    @Override
    public boolean hasPrevious(Object container) {
        return page(container).getPageable().getOffset() > 0;
    }

    @Override
    public boolean hasNext(Object container) {
        Page<?> page = page(container);
        return page.getPageable().getOffset() + page.getNumberOfElements() < page.getTotalElements();
    }

    @Override
    public String cursorAt(Object container, int index) {
        Page<?> page = page(container);
        ScrollPosition position = ScrollPosition.offset(page.getPageable().getOffset() + index);
        return getCursorStrategy().toCursor(position);
    }

    @Override
    public <T> Object createConnection(Object container, List<Edge<T>> edges, PageInfo pageInfo) {
        return new PageConnection<>(
                edges, pageInfo, Math.toIntExact(page(container).getTotalElements()));
    }

    @SuppressWarnings("unchecked")
    private <T> Page<T> page(Object container) {
        return (Page<T>) container;
    }
}
