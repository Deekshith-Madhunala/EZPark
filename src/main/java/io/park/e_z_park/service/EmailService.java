package io.park.e_z_park.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Method to send a simple email
    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@gmail.com");  // Your email address
        message.setTo(toEmail);  // User's email address
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
