package org.bk.books.web;

import org.bk.books.service.SampleDataService;
import org.bk.books.web.dto.LoadSampleDataPayload;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class SampleDataController {
  private final SampleDataService sampleDataService;

  public SampleDataController(SampleDataService sampleDataService) {
    this.sampleDataService = sampleDataService;
  }

  @MutationMapping
  public LoadSampleDataPayload loadSampleData() {
    return LoadSampleDataPayload.map(sampleDataService.loadSampleData());
  }
}
