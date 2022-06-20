package com.XiangQi.XiangQiBE.Components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class Mailer {
    @Autowired
    private JavaMailSender emailSender;

    public void SendHTMLEmail(String to, String subject, String html) {
        var msg = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg);
        try {
            helper.setText(html, true);
            helper.setSubject(subject);
            helper.setTo(to);

            emailSender.send(msg);
        } catch (Exception e) {}
    }
}
