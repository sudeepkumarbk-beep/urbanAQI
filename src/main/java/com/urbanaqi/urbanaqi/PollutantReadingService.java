
package com.urbanaqi.urbanaqi;

import org.springframework.stereotype.Service;

@Service
public class PollutantReadingService {

    private final PollutantReadingRepository repository;

    public PollutantReadingService(
            PollutantReadingRepository repository) {
        this.repository = repository;
    }

    public void saveReading(
            String parameter,
            double value,
            String unit,
            Integer sensorId,
            java.time.Instant measuredAt) {

        if (parameter == null || unit == null || measuredAt == null) {
            return;
        }

        boolean alreadyExists =
                repository.existsBySensorIdAndMeasuredAtAndParameter(
                        sensorId, measuredAt, parameter
                );

        if (!alreadyExists) {
            PollutantReading reading = new PollutantReading(
                    parameter,
                    value,
                    unit,
                    sensorId,
                    measuredAt
            );

            repository.save(reading);
        }
    }
}
