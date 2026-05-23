package org.bk.graphql.web.dto;

import org.bk.graphql.service.SampleDataLoadResult;

public record LoadSampleDataPayload(int authorsLoaded, int booksLoaded) {
    public static LoadSampleDataPayload map(SampleDataLoadResult result) {
        return new LoadSampleDataPayload(result.authorsLoaded(), result.booksLoaded());
    }
}
