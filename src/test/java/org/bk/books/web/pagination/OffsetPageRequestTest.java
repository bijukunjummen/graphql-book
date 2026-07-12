package org.bk.books.web.pagination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class OffsetPageRequestTest {
    @Test
    void test_of_withOffsetPageSizeAndSort_returnsRequestWithExactValues() {
        Sort sort = Sort.by("name").descending();

        OffsetPageRequest request = OffsetPageRequest.of(7, 3, sort);

        assertThat(request.getOffset()).isEqualTo(7);
        assertThat(request.getPageSize()).isEqualTo(3);
        assertThat(request.getPageNumber()).isEqualTo(2);
        assertThat(request.getSort()).isEqualTo(sort);
        assertThat(request.hasPrevious()).isTrue();
    }

    @Test
    void test_offsetPageRequestNavigation_withOffsetRequest_returnsExpectedOffsets() {
        OffsetPageRequest request = OffsetPageRequest.of(5, 2, Sort.by("name"));

        Pageable next = request.next();
        Pageable previous = request.previousOrFirst();
        Pageable first = request.first();
        Pageable fourthPage = request.withPage(4);

        assertThat(next.getOffset()).isEqualTo(7);
        assertThat(previous.getOffset()).isEqualTo(3);
        assertThat(first.getOffset()).isZero();
        assertThat(fourthPage.getOffset()).isEqualTo(8);
    }

    @Test
    void test_offsetPageRequestValidation_withInvalidArguments_throwsIllegalArgumentException() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> OffsetPageRequest.of(-1, 10))
                .withMessage("Offset must not be negative");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> OffsetPageRequest.of(0, 0))
                .withMessage("Page size must be greater than zero");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> OffsetPageRequest.of(0, 10).withPage(-1))
                .withMessage("Page index must not be less than zero");
    }
}
