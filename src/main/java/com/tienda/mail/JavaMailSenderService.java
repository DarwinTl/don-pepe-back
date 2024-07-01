package com.tienda.mail;

import java.util.Map;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class JavaMailSenderService {

	public JavaMailSender mailSender;

	public TemplateEngine templateEngine;

	public JavaMailSenderService(JavaMailSender mailSender, TemplateEngine templateEngine) {
		this.mailSender = mailSender;
		this.templateEngine = templateEngine;
	}

	public void enviarCorreo1(Email mail) {

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(mail.getTo());
		message.setSubject(mail.getSubject());
		message.setText(mail.getBody());
		mailSender.send(message);
	}

	public void enviarCorreo(Email mail, Map<String, Object> datos) throws MessagingException {

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		helper.setTo(mail.getTo());
		helper.setSubject(mail.getSubject());
		Context context = new Context();
		context.setVariables(datos);
		String htmlMsg = templateEngine.process("template", context);

		helper.setText(htmlMsg, true);

		mailSender.send(mimeMessage);

	}

}
