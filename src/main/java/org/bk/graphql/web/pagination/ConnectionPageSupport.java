package org.bk.graphql.web.pagination;

import org.bk.graphql.web.dto.OrderField;
import org.bk.graphql.web.dto.SortInput;
import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class ConnectionPageSupport {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT_FIELD = "name";

    public <T, R> Page<R> page(
        ScrollSubrange subrange,
        List<SortInput> sort,
        Function<Pageable, Page<T>> fetch,
        Function<T, R> mapper
    ) {
        int limit = subrange.count().orElse(DEFAULT_PAGE_SIZE);
        int offset = offset(subrange);
        Sort pageSort = toSort(sort);

        return fetch.apply(OffsetPageRequest.of(offset, limit, pageSort)).map(mapper);
    }

    private int offset(ScrollSubrange subrange) {
        ScrollPosition position = subrange.position().orElse(ScrollPosition.offset());
        if (!(position instanceof OffsetScrollPosition offsetPosition)) {
            throw new IllegalArgumentException("Only offset-based cursors are supported");
        }
        return offsetPosition.isInitial() ? 0 : (int)offsetPosition.getOffset() + 1;
    }

    private Sort toSort(List<SortInput> sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.by(DEFAULT_SORT_FIELD).ascending();
        }

        List<Sort.Order> orders = sort.stream()
            .filter(input -> input.field() != null && !input.field().isBlank())
            .map(input -> new Sort.Order(direction(input), input.field()))
            .toList();

        return orders.isEmpty() ? Sort.by(DEFAULT_SORT_FIELD).ascending() : Sort.by(orders);
    }

    private Sort.Direction direction(SortInput input) {
        return input.order() == OrderField.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;
    }
}
