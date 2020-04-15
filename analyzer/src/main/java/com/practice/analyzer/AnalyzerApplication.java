package com.practice.analyzer;

import com.practice.analyzer.taskscheduler.RunnableTask;
import com.practice.analyzer.wave.model.WeatherDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@SpringBootApplication
@RestController
public class AnalyzerApplication implements CommandLineRunner {

	@Autowired
	private ApplicationContext applicationContext;

	public static void main(String[] args) {

		SpringApplication.run(AnalyzerApplication.class, args);

	}


	@Override
	public void run(String... args) throws Exception {
		ThreadPoolTaskScheduler taskScheduler = applicationContext.getBean(ThreadPoolTaskScheduler.class);
		CronTrigger cronTrigger = new CronTrigger("10 * * * * ?");
		taskScheduler.schedule(new RunnableTask("once a minute"),cronTrigger);
	}
}
