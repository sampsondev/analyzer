package com.practice.analyzer.wave;

import com.practice.analyzer.wave.model.WeatherDay;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WaveController {

    @GetMapping("/walkTheDog")
    WeatherDay wave(){
        return new WeatherDay(60,20,"sunny");
    }
}
