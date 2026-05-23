package org.bk.graphql.web;

import org.bk.graphql.service.SampleDataService;
import org.bk.graphql.web.dto.LoadSampleDataPayload;
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
