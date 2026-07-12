package org.bk.books.web.dto;

import org.bk.books.service.SampleDataLoadResult;

public record LoadSampleDataPayload(int authorsLoaded, int booksLoaded) {
  public static LoadSampleDataPayload map(SampleDataLoadResult result) {
    return new LoadSampleDataPayload(result.authorsLoaded(), result.booksLoaded());
  }
}
