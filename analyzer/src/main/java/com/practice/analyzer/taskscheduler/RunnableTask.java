package com.practice.analyzer.taskscheduler;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.practice.analyzer.wave.model.TheWeather;
import com.practice.analyzer.wave.model.WeatherDay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

import java.util.Collections;
import java.util.function.Consumer;

public class RunnableTask implements Runnable{

    private final AmazonSQS sqs;
    private String message;
    private Logger logger = LoggerFactory.getLogger(RunnableTask.class);
    private MonoProcessor<TheWeather> processor;


    public RunnableTask(String message){
        this.message = message;
        sqs = AmazonSQSClientBuilder.defaultClient();
    }

    @Override
    public void run() {
        WebClient client = WebClient.builder()
                .baseUrl("http://api.weatherstack.com")
                .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://api.weatherstack.com"))
                .build();
        String access_key = System.getenv("access_key");
        Mono<TheWeather> mono = client.get()
                .uri("/current?access_key="+ access_key + "&query=Tampa")
                .retrieve()
                .bodyToMono(TheWeather.class);
        this.processor = mono.doOnSuccess(new Consumer<TheWeather>() {
            @Override
            public void accept(TheWeather weatherDay) {
                WeatherDay today = new WeatherDay();
                today.high = weatherDay.getCurrent().getTemperature();
                today.conditions="don't walk the dog";
                today.conditions = weatherDay.getCurrent()
                        .getWeatherDescriptions().stream()
                        .map(c -> c.toLowerCase())
                        .filter(c -> c.contains("rain"))
                        .findFirst().orElse("walk the dog");
                logger.info("temp is " + weatherDay.getCurrent().getTemperature());
                SendMessageRequest msg_request = new SendMessageRequest()
                        .withQueueUrl(System.getenv("queueURL"))
                        .withMessageBody(new Gson().toJson(today));
                sqs.sendMessage(msg_request);
            }
        })
                .doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                logger.error("Error " + throwable.getMessage());
            }
        }).toProcessor();
    }
}
