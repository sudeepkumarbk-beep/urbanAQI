
package com.urbanaqi.urbanaqi;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SensorController {

    private final OpenAQService openAQService;

    public SensorController(OpenAQService openAQService) {
        this.openAQService = openAQService;
    }

    @GetMapping("/sensors")
    public String sensors(Model model) {

        AirQualityData data = openAQService.getAirQualityData();

        model.addAttribute("pm25", data.getPm25());
        model.addAttribute("pm10", data.getPm10());
        model.addAttribute("co", data.getCo());
        model.addAttribute("no2", data.getNo2());
        model.addAttribute("so2", data.getSo2());
        model.addAttribute("o3", data.getO3());

        model.addAttribute("lastUpdated",
                java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")));

        return "sensors";
    }
}