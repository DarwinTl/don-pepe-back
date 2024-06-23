package com.tienda.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class JavaMailSenderService {

	public JavaMailSender mailSender;

	public JavaMailSenderService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void enviarCorreo(Email mail) {

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(mail.getTo());
		message.setSubject(mail.getSubject());
		message.setText(mail.getBody());
		mailSender.send(message);
	}

}
