package com.urbanaqi.urbanaqi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class HomeController {

    private final OpenAQService openAQService;
    private final AQIHistoryService aqiHistoryService;

    public HomeController(OpenAQService openAQService,
                          AQIHistoryService aqiHistoryService) {
        this.openAQService = openAQService;
        this.aqiHistoryService = aqiHistoryService;
    }

    @GetMapping("/")
    public String home(Model model) {

        // Get real-time pollution data from OpenAQ
        AirQualityData data = openAQService.getAirQualityData();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("hh:mm a");

        String lastUpdated =
                LocalDateTime.now().format(formatter);

        model.addAttribute("lastUpdated", lastUpdated);

        // Calculate AQI
        int aqi = AQICalculator.calculateAQI(data);

        // Get AQI category
        String status = AQICalculator.getAQIStatus(aqi);

        // Find main pollutant
        String mainPollutant =
                AQICalculator.getMainPollutant(data);

        // Generate health alert
        String alert;

        if (aqi <= 50) {
            alert = "Air quality is good. Outdoor activities are safe.";
        }
        else if (aqi <= 100) {
            alert = "Air quality is satisfactory. Sensitive individuals should take normal precautions.";
        }
        else if (aqi <= 200) {
            alert = "Air quality is moderately polluted. Sensitive individuals should reduce prolonged outdoor activity.";
        }
        else if (aqi <= 300) {
            alert = "Air quality is poor. Avoid prolonged outdoor exposure, especially for sensitive groups.";
        }
        else if (aqi <= 400) {
            alert = "Air quality is very poor. Avoid outdoor activities and take necessary precautions.";
        }
        else {
            alert = "Air quality is severe. Avoid outdoor exposure and take immediate precautions.";
        }

        // Save AQI reading to history
        aqiHistoryService.addReading(
                aqi,
                status,
                mainPollutant
        );

        // Send AQI information to webpage
        model.addAttribute("aqi", aqi);

        double gaugePosition =
                (aqi / 500.0) * 100;

        model.addAttribute("gaugePosition", gaugePosition);
        model.addAttribute("status", status);
        model.addAttribute("mainPollutant", mainPollutant);
        model.addAttribute("alert", alert);

        // Send pollutant values to webpage
        model.addAttribute("pm25", data.getPm25());
        model.addAttribute("pm10", data.getPm10());
        model.addAttribute("co", data.getCo());
        model.addAttribute("no2", data.getNo2());
        model.addAttribute("so2", data.getSo2());
        model.addAttribute("o3", data.getO3());

        // Send AQI history to webpage
        model.addAttribute(
                "history",
                aqiHistoryService.getHistory()
        );
        List<Map<String, Object>> chartHistory = new ArrayList<>();

        for (AQIHistory reading : aqiHistoryService.getHistory()) {
            Map<String, Object> item = new HashMap<>();

            item.put("aqi", reading.getAqi());
            item.put("time", reading.getTime().toString());

            chartHistory.add(item);
        }

        model.addAttribute("chartHistory", chartHistory);

        return "index";
    }
}