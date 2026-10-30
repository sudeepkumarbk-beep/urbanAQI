
package com.urbanaqi.urbanaqi;
import jakarta.persistence.Column;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "pollutant_readings")
public class PollutantReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String parameter;

    @Column(name = "pollutant_value")
    private double value;

    private String unit;

    private Integer sensorId;

    private Instant measuredAt;

    public PollutantReading() {
    }

    public PollutantReading(String parameter, double value,
                            String unit, Integer sensorId,
                            Instant measuredAt) {
        this.parameter = parameter;
        this.value = value;
        this.unit = unit;
        this.sensorId = sensorId;
        this.measuredAt = measuredAt;
    }

    public Long getId() {
        return id;
    }

    public String getParameter() {
        return parameter;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public Integer getSensorId() {
        return sensorId;
    }

    public Instant getMeasuredAt() {
        return measuredAt;
    }
}
