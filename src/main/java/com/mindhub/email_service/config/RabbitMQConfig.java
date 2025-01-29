package com.mindhub.email_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Collections;
import java.util.Properties;

@Configuration
public class RabbitMQConfig {
    // Injecting email credentials from application properties
    @Value("${EMAIL}")
    private String EMAIL;

    @Value("${EMAIL_PASSWORD}")
    private String EMAIL_PASSWORD;
    // Define a RabbitMQ queue for "orderCreatedEvent"
    @Bean
    public Queue queueOrderCreatedEvent() {
        return new Queue("orderCreatedEvent", false);
    }
    // Define a RabbitMQ queue for "userRegister"
    @Bean
    public Queue queueUserRegister() {
        return new Queue("userRegister", false);
    }
    // Define a RabbitMQ queue for testing purposes
    @Bean
    public Queue queue() {
        return new Queue("testingQueue1", false);
    }
    @Bean
    public Queue queue2() {
        return new Queue("testingQueue2", false);
    }
    // Define a RabbitMQ topic exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("testingExchange");
    }
    // Bind the "orderCreatedEvent" queue to the exchange with a routing key
    @Bean
    public Binding bindingQueueOrderCreatedEvent(Queue queueOrderCreatedEvent, TopicExchange exchange) {
        return BindingBuilder.bind(queueOrderCreatedEvent).to(exchange).with("routingOrderCreatedEvent.key");
    }
    // Bind the "userRegister" queue to the exchange with a routing key
    @Bean
    public Binding bindingQueueUserRegister(Queue queueUserRegister, TopicExchange exchange) {
        return BindingBuilder.bind(queueUserRegister).to(exchange).with("routingUserRegister.key");
    }
    // Bind the "testingQueue1" queue to the exchange with a routing key
    @Bean
    public Binding bindingQueue(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("routing.key");
    }
    // Bind the "testingQueue2" queue to the exchange with a routing key
    @Bean
    public Binding bindingQueue2(Queue queue2, TopicExchange exchange) {
        return BindingBuilder.bind(queue2).to(exchange).with("routing.key2");
    }
    // Configure a JSON message converter for RabbitMQ messages
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    // Configure RabbitTemplate with the JSON message converter
    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter); // Configura JSON como convertidor
        return rabbitTemplate;
    }
    // Configure JavaMailSender for sending emails
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com"); // SMTP host for Gmail
        mailSender.setPort(587); // SMTP port for Gmail

        mailSender.setUsername(EMAIL); // Set email username
        mailSender.setPassword(EMAIL_PASSWORD); // Set email password
        // Configure additional properties for the mail sender
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp"); // Use SMTP protocol
        props.put("mail.smtp.auth", "true"); // Enable SMTP authentication
        props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS encryption
        props.put("mail.debug", "true"); // Enable debug mode for troubleshooting

        return mailSender;
    }
}
