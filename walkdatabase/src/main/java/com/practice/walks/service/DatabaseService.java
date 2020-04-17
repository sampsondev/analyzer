package com.practice.walks.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

@Service
public class DatabaseService {
    private  AmazonSQS sqs;
    private  AmazonDynamoDB client;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    @Value("${aws.queueKey}")
    private String queueKey;

    @Value("${aws.queueSecret}")
    private String queueSecret;



    public void startReading( ){
        AWSCredentials credentials = new BasicAWSCredentials(queueKey, queueSecret);
        sqs = AmazonSQSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
        client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(System.getenv("dynamoURL"), "us-east-1")
                ).build();
        try {
            while(true) {
                List<Message> messages = sqs.receiveMessage(System.getenv("queueURL")).getMessages();
                if(messages!=null && !messages.isEmpty()){
                    messages.stream()
                            .forEach(m ->{
                                logger.info("processing " + m);
                                HashMap<String, AttributeValue> value = new HashMap<String, AttributeValue>();
                                WeatherDay day =  new Gson().fromJson(m.getBody(),WeatherDay.class);
                                value.put("recommendation",new AttributeValue(day.conditions) );
                                value.put("high", new AttributeValue(day.high.toString()));
                                try {
                                    client.putItem("WalkWeather", value);
                                } catch (Exception e) {
                                    logger.error("error writing to table " + e.getMessage());
                                    e.printStackTrace();
                                }
                            });
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }

    }

}
