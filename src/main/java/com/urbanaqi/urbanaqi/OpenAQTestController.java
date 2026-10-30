package com.urbanaqi.urbanaqi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAQTestController {

    private final OpenAQService openAQService;

    public OpenAQTestController(OpenAQService openAQService) {
        this.openAQService = openAQService;
    }

    @GetMapping("/test-openaq")
    public AirQualityData testOpenAQ() {
        return openAQService.getAirQualityData();
    }
}