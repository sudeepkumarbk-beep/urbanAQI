
package com.urbanaqi.urbanaqi;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportsController {

    private final AQIHistoryService aqiHistoryService;

    public ReportsController(AQIHistoryService aqiHistoryService) {
        this.aqiHistoryService = aqiHistoryService;
    }

    @GetMapping("/reports")
    public String reports(Model model) {

        model.addAttribute(
                "history",
                aqiHistoryService.getHistory()
        );

        return "reports";
    }
}