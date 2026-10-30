
package com.urbanaqi.urbanaqi;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AlertsController {

    private final OpenAQService openAQService;

    public AlertsController(OpenAQService openAQService) {
        this.openAQService = openAQService;
    }

    @GetMapping("/alerts")
    public String alerts(Model model) {

        AirQualityData data = openAQService.getAirQualityData();

        int aqi = AQICalculator.calculateAQI(data);
        String status = AQICalculator.getAQIStatus(aqi);

        String alert;

        if (aqi <= 50) {
            alert = "Air quality is good. Outdoor activities are generally safe.";
        } else if (aqi <= 100) {
            alert = "Air quality is satisfactory. Sensitive individuals should take normal precautions.";
        } else if (aqi <= 200) {
            alert = "Air quality is moderately polluted. Sensitive individuals should reduce prolonged outdoor activity.";
        } else if (aqi <= 300) {
            alert = "Air quality is poor. Reduce prolonged outdoor exposure.";
        } else if (aqi <= 400) {
            alert = "Air quality is very poor. Avoid prolonged outdoor activities.";
        } else {
            alert = "Air quality is severe. Avoid outdoor exposure where possible.";
        }

        model.addAttribute("aqi", aqi);
        model.addAttribute("status", status);
        model.addAttribute("alert", alert);

        return "alerts";
    }
}