package com.example.bankpick.services;

import com.example.bankpick.BuildConfig;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import android.util.Log;

public class MailService {
    private static final String TAG = "MailService";
    private final String host;
    private final String port;
    private final String user;
    private final String pass;
    private final String sender;

    public MailService() {
        // Use the values injected by Gradle at build time from local.properties
        this.host = BuildConfig.SMTP_HOST;
        this.port = BuildConfig.SMTP_PORT;
        this.user = BuildConfig.SMTP_USER;
        this.pass = BuildConfig.SMTP_PASS;
        this.sender = BuildConfig.SMTP_SENDER;
    }

    public void sendEmail(String to, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        new Thread(() -> {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sender));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);
                Log.d(TAG, "Email sent successfully to: " + to);
            } catch (MessagingException e) {

                Log.e(TAG, "Failed to send email", e);
            }
        }).start();
    }
}
