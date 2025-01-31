package com.mindhub.email_service.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

//Consumer
@Service
public class AppService {
    /*
    Only use for testing the rabbit server
     */

    @RabbitListener(queues = "testingQueue1")
    public void listenerQueue1(String message){
        System.out.println("Mensaje de testingQueue: " + message);
    }

    @RabbitListener(queues = "testingQueue2")
    public void listenerQueue2(Long id){
        System.out.println("Id en testingQueue2: " + id);
    }

}