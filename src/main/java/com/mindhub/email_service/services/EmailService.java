package com.mindhub.email_service.services;

import com.mindhub.email_service.dtos.NewUserDTO;
import com.mindhub.email_service.dtos.OrderCreatedEvent;
import com.mindhub.email_service.models.PdfGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

// email-service is the Listener/Consumer/Subscriber
@Service
public class EmailService {
    // Inject the JavaMailSender bean
    @Autowired
    private JavaMailSender mailSender;
    // Inject the email address from application properties
    @Value("${EMAIL}")
    private String EMAIL;
    // Constructor injection for JavaMailSender
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    // RabbitMQ listener for the "userRegister" queue
    @RabbitListener(queues = "userRegister")
    public void listenerUserRegisterQueue(NewUserDTO userDTO){
        // Create a welcome email message
        String emailContent = "Welcome, " + userDTO.username() + "! Thank you for your registration!.";
        sendEmail(userDTO.email(), "Welcome", emailContent);
    }
    // RabbitMQ listener for the "orderCreatedEvent" queue
    @RabbitListener(queues = "orderCreatedEvent")
    public void listenerOrderCreatedEventQueue(OrderCreatedEvent order) {
        try {
            // Generate a PDF using Apache PDFBox
            byte[] pdfBytes = PdfGenerator.generatePdf(order);
            // Send an email with the PDF attachment
            sendEmailOrder(order.email(), pdfBytes, "Order_" + order.id() + ".pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to send an email with an attached PDF
    private void sendEmailOrder(String to, byte[] pdfBytes, String fileName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // Enable multipart messages

        helper.setTo(to); // Set recipient email
        helper.setSubject("Your Order Confirmation"); // Set email subject
        helper.setText("Dear Customer,\n\nPlease find your order details attached.", false); // Set email body
        // Attach the PDF to the email
        helper.addAttachment(fileName, () -> new java.io.ByteArrayInputStream(pdfBytes));

        mailSender.send(message);
    }
    // Method to send a simple email (without attachments)
    private void sendEmail(String to, String subject, String content) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(EMAIL);// Set sender email
        email.setTo(to); // Set recipient email
        email.setSubject(subject); // Set email subject
        email.setText(content); // Set email content
        System.out.println("Sending email to " + to); // Log the email sending process
        mailSender.send(email);  // Send the email
    }
}