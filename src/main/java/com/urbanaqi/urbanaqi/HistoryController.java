
package com.urbanaqi.urbanaqi;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HistoryController {

    private final AQIHistoryService aqiHistoryService;

    public HistoryController(AQIHistoryService aqiHistoryService) {
        this.aqiHistoryService = aqiHistoryService;
    }

    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("history", aqiHistoryService.getHistory());
        return "history";
    }
}