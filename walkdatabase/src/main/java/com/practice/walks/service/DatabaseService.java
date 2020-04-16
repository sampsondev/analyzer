package com.practice.walks.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {
    private final AmazonSQS sqs;

    public DatabaseService() {
        sqs = AmazonSQSClientBuilder.defaultClient();

    }
//subscribe to the queue

    public void startReading( ){
        try {
            while(true) {
                List<Message> messages = sqs.receiveMessage(System.getenv("queueUrl")).getMessages();
                if(messages!=null && !messages.isEmpty()){
                    messages.stream()
                            .forEach(m -> m.getBody());
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
