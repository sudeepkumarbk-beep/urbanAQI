
package com.urbanaqi.urbanaqi;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "aqi_history")
public class AQIHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int aqi;

    private String status;

    private String mainPollutant;

    private LocalDateTime time;

    public AQIHistory() {
    }

    public AQIHistory(int aqi, String status,
                      String mainPollutant, LocalDateTime time) {
        this.aqi = aqi;
        this.status = status;
        this.mainPollutant = mainPollutant;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public int getAqi() {
        return aqi;
    }

    public String getStatus() {
        return status;
    }

    public String getMainPollutant() {
        return mainPollutant;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
