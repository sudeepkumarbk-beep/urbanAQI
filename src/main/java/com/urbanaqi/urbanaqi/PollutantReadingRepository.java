
package com.urbanaqi.urbanaqi;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface PollutantReadingRepository
        extends JpaRepository<PollutantReading, Long> {

    List<PollutantReading>
    findByParameterAndMeasuredAtAfterOrderByMeasuredAtAsc(
            String parameter,
            Instant startTime
    );
    boolean existsBySensorIdAndMeasuredAtAndParameter(
            Integer sensorId,
            Instant measuredAt,
            String parameter
    );}
