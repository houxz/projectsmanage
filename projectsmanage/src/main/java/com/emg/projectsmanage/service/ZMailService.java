package com.emg.projectsmanage.service;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.emg.projectsmanage.config.ZMailConfig;

@Service
public class ZMailService {

	private static final Logger logger = LoggerFactory.getLogger(ZMailService.class);

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private ZMailConfig zMailConfig;

	public void sendRichEmail(String subject, String text) {
		try {
			if (zMailConfig == null || !zMailConfig.getEnabled())
				return;
			
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
			helper.setFrom(zMailConfig.getFrom());
			
			if (zMailConfig.getTos() == null || zMailConfig.getTos().size() <= 0) {
				logger.error("No config in zMailConfig.tos");
				return;
			}
			List<InternetAddress> tos = new ArrayList<InternetAddress>();
			for (String to : zMailConfig.getTos()) {
				tos.add(new InternetAddress(to));
			}
			helper.setTo(tos.toArray(new InternetAddress[tos.size()]));
			helper.setSubject(subject);
			helper.setText(text, true);

			mailSender.send(message);

			logger.debug("From : " + message.getFrom());
			for (Address to : message.getAllRecipients()) {
				logger.debug("To   : " + to);
			}
			logger.debug("Subj : " + message.getSubject());
			logger.debug("Text : " + text);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
