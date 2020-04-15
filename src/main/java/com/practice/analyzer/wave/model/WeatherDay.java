package com.practice.analyzer.wave.model;

import lombok.Data;

@Data
public class WeatherDay {
    public Integer high;
    public Integer low;
    public String conditions;

    public WeatherDay() {
    }

    public WeatherDay(int i, int i1, String sunny) {
        this.high=i;
        this.low=i1;
        conditions=sunny;
    }
}
