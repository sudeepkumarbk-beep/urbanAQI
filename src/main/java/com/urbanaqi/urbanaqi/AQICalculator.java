
package com.urbanaqi.urbanaqi;

public class AQICalculator {

    public static int calculateAQI(AirQualityData data) {

        if (data == null) {
            throw new IllegalArgumentException(
                    "Air quality data is unavailable."
            );
        }

        int pm25AQI = calculatePM25AQI(data.getPm25());
        int pm10AQI = calculatePM10AQI(data.getPm10());

        return Math.max(pm25AQI, pm10AQI);
    }

    private static int calculatePM25AQI(double value) {

        double[] concentration = {0, 30, 60, 90, 120, 250, 500};
        int[] index = {0, 50, 100, 200, 300, 400, 500};

        return calculateSubIndex(value, concentration, index);
    }

    private static int calculatePM10AQI(double value) {

        double[] concentration = {0, 50, 100, 250, 350, 430, 600};
        int[] index = {0, 50, 100, 200, 300, 400, 500};

        return calculateSubIndex(value, concentration, index);
    }

    private static int calculateSubIndex(
            double value,
            double[] concentration,
            int[] index) {

        if (!Double.isFinite(value) || value < 0) {
            return 0;
        }

        for (int i = 0; i < concentration.length - 1; i++) {

            if (value <= concentration[i + 1]) {

                double result = index[i]
                        + (value - concentration[i])
                        * (index[i + 1] - index[i])
                        / (concentration[i + 1] - concentration[i]);

                return (int) Math.round(result);
            }
        }

        return 500;
    }

    public static String getAQIStatus(int aqi) {

        if (aqi <= 50) return "Good";
        if (aqi <= 100) return "Satisfactory";
        if (aqi <= 200) return "Moderate";
        if (aqi <= 300) return "Poor";
        if (aqi <= 400) return "Very Poor";

        return "Severe";
    }

    public static String getMainPollutant(AirQualityData data) {

        if (data == null) {
            return "Unavailable";
        }

        int pm25AQI = calculatePM25AQI(data.getPm25());
        int pm10AQI = calculatePM10AQI(data.getPm10());

        return pm25AQI >= pm10AQI ? "PM2.5" : "PM10";
    }
}
