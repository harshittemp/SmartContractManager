package com.smart.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import java.util.Properties;

@Service
public class EmailService {
	public boolean sendEmail(String subject, String msg, String to) {

        boolean flag=false;
        // Variable for Gmail
        String host = "smtp.gmail.com";
        String from = "iamhimanshu448@gmail.com";
        String password = "ucfn fkjk enll wwhz";

        // Get the system properties
        Properties properties = System.getProperties();
        System.out.println("Properties: " + properties);

        // Setting important information to properties object
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Step 1: Get the session object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        // Step 2: Compose the message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(from);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            //message.setText(msg);
            message.setContent(msg,"text/html");

            // Step 3: Send the message
            Transport.send(message);
            System.out.println("Email sent successfully...");
            flag=true;

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return flag;
    }

}
