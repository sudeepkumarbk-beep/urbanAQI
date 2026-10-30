
package com.urbanaqi.urbanaqi;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AQIHistoryService {

    private final AQIHistoryRepository repository;

    public AQIHistoryService(AQIHistoryRepository repository) {
        this.repository = repository;
    }

    public void addReading(int aqi, String status,
                           String mainPollutant) {

        AQIHistory reading = new AQIHistory(
                aqi,
                status,
                mainPollutant,
                LocalDateTime.now()
        );

        repository.save(reading);
    }

    public List<AQIHistory> getHistory() {
        return repository.findAllByOrderByTimeDesc();
    }
}
