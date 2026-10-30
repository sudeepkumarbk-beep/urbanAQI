package com.urbanaqi.urbanaqi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class OpenAQService {

    @Value("${openaq.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PollutantReadingService pollutantReadingService;
    public OpenAQService(PollutantReadingService pollutantReadingService) {
        this.pollutantReadingService = pollutantReadingService;
    }

    public AirQualityData getAirQualityData() {

        try {

            int locationId = 5548;

            HttpClient client = HttpClient.newHttpClient();

            // -------------------------------------------------
            // STEP 1: Get sensor information
            // -------------------------------------------------

            String sensorsUrl =
                    "https://api.openaq.org/v3/locations/"
                            + locationId + "/sensors";

            HttpRequest sensorsRequest = HttpRequest.newBuilder()
                    .uri(URI.create(sensorsUrl))
                    .header("X-API-Key", apiKey)
                    .GET()
                    .build();

            HttpResponse<String> sensorsResponse =
                    client.send(
                            sensorsRequest,
                            HttpResponse.BodyHandlers.ofString()
                    );

            JsonNode sensorsJson =
                    objectMapper.readTree(sensorsResponse.body());

            // Sensor ID → pollutant
            Map<Integer, String> sensorParameters = new HashMap<>();

            // Sensor ID → unit
            Map<Integer, String> sensorUnits = new HashMap<>();

            for (JsonNode sensor : sensorsJson.get("results")) {

                int sensorId = sensor.get("id").asInt();

                String parameter =
                        sensor.get("parameter")
                                .get("name")
                                .asText();

                String units =
                        sensor.get("parameter")
                                .get("units")
                                .asText();

                sensorParameters.put(sensorId, parameter);
                sensorUnits.put(sensorId, units);
            }


            // -------------------------------------------------
            // STEP 2: Get latest measurements
            // -------------------------------------------------

            String latestUrl =
                    "https://api.openaq.org/v3/locations/"
                            + locationId + "/latest";

            HttpRequest latestRequest = HttpRequest.newBuilder()
                    .uri(URI.create(latestUrl))
                    .header("X-API-Key", apiKey)
                    .GET()
                    .build();

            HttpResponse<String> latestResponse =
                    client.send(
                            latestRequest,
                            HttpResponse.BodyHandlers.ofString()
                    );

            JsonNode latestJson =
                    objectMapper.readTree(latestResponse.body());
            System.out.println("OpenAQ latest API status: "
                    + latestResponse.statusCode());

            System.out.println("OpenAQ latest API response:");
            System.out.println(latestResponse.body());


            // -------------------------------------------------
            // STEP 3: Store pollutant values
            // -------------------------------------------------

            Map<String, JsonNode> newestReadings = new HashMap<>();
            Map<String, String> newestUnits = new HashMap<>();
            Map<String, Instant> newestTimes = new HashMap<>();

            for (JsonNode reading : latestJson.get("results")) {

                int sensorId = reading.get("sensorsId").asInt();

                String parameter = sensorParameters.get(sensorId);
                String units = sensorUnits.get(sensorId);
                System.out.println("Sensor " + sensorId
                        + " | Parameter: " + parameter
                        + " | Units: " + units);

                if (parameter == null || units == null) {
                    continue;
                }

                String timestamp = reading.path("datetime")
                        .path("utc").asText();

                if (timestamp.isEmpty()) {
                    continue;
                }

                Instant readingTime = Instant.parse(timestamp);
                // Save supported pollutant measurements to the database
                if (isSupportedPollutant(parameter)
                        && reading.hasNonNull("value")) {

                    double measuredValue = reading.get("value").asDouble();

                    pollutantReadingService.saveReading(
                            parameter,
                            measuredValue,
                            units,
                            sensorId,
                            readingTime
                    );
                }

                if (!newestTimes.containsKey(parameter)
                        || readingTime.isAfter(newestTimes.get(parameter))) {

                    newestTimes.put(parameter, readingTime);
                    newestReadings.put(parameter, reading);
                    newestUnits.put(parameter, units);
                }
            }
            double pm25 = getValue(newestReadings, newestUnits, "pm25", "µg/m³");
            double pm10 = getValue(newestReadings, newestUnits, "pm10", "µg/m³");
            double no2 = getValue(newestReadings, newestUnits, "no2", "ppb");
            double so2 = getValue(newestReadings, newestUnits, "so2", "ppb");
            double o3 = getValue(newestReadings, newestUnits, "o3", "µg/m³");
            double co = getValue(newestReadings, newestUnits, "co", "ppb") / 1000.0;


            // -------------------------------------------------
            // STEP 4: Create AirQualityData object
            // -------------------------------------------------

            return new AirQualityData(
                    pm25,
                    pm10,
                    co,
                    no2,
                    so2,
                    o3
            );

        } catch (Exception e) {

            System.err.println("Error fetching OpenAQ data: "
                    + e.getMessage());

            throw new RuntimeException(
                    "Unable to fetch air quality data. Please try again later.",
                    e
            );
        }
    }

    private double getValue(
            Map<String, JsonNode> readings,
            Map<String, String> units,
            String parameter,
            String expectedUnit) {

        JsonNode reading = readings.get(parameter);

        if (reading == null || !expectedUnit.equals(units.get(parameter))) {
            return 0;
        }

        return reading.get("value").asDouble();
    } private boolean isSupportedPollutant(String parameter) {
        return parameter.equals("pm25")
                || parameter.equals("pm10")
                || parameter.equals("co")
                || parameter.equals("no2")
                || parameter.equals("so2")
                || parameter.equals("o3");
    }}