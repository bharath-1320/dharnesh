package com.creativespacefinder.manhattan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ForecastResponse {

    @JsonProperty("list")
    private List<HourlyForecast> hourly;

    public List<HourlyForecast> getHourly() {
        return hourly;
    }

    public void setHourly(List<HourlyForecast> hourly) {
        this.hourly = hourly;
    }

    public static class HourlyForecast {
        private long dt;
        
        @JsonProperty("main")
        private TempInfo tempInfo;

        private List<Weather> weather;

        public long getDt() {
            return dt;
        }

        public void setDt(long dt) {
            this.dt = dt;
        }

        public List<Weather> getWeather() {
            return weather;
        }

        public void setWeather(List<Weather> weather) {
            this.weather = weather;
        }

        public void setTempInfo(TempInfo tempInfo) {
            this.tempInfo = tempInfo;
        }

        public String getReadableTime() {
            return Instant.ofEpochSecond(dt)
                    .atZone(ZoneId.of("America/New_York"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

        public double getTemp() {
            return tempInfo != null ? tempInfo.getTemp() : 0.0;
        }

        public String getCondition() {
            return weather != null && !weather.isEmpty() ? weather.get(0).getMain() : "Unknown";
        }

        public static class TempInfo {
            private double temp;
            
            public double getTemp() {
                return temp;
            }

            public void setTemp(double temp) {
                this.temp = temp;
            }
        }
    }

    public static class Weather {
        private String main;
        private String description;

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}