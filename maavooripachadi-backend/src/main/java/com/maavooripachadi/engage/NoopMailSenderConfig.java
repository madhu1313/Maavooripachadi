package com.maavooripachadi.engage;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

@Configuration
class NoopMailSenderConfig {

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    JavaMailSender noopMailSender() {
        return new NoopMailSender();
    }

    private static final class NoopMailSender implements JavaMailSender {
        private static final Logger log = LoggerFactory.getLogger(NoopMailSender.class);
        private final Session session = Session.getInstance(new Properties());

        @Override
        public MimeMessage createMimeMessage() {
            return new MimeMessage(session);
        }

        @Override
        public MimeMessage createMimeMessage(InputStream contentStream) {
            try {
                return new MimeMessage(session, contentStream);
            } catch (Exception ex) {
                throw new MailPreparationException("Failed to create MimeMessage from stream", ex);
            }
        }

        @Override
        public void send(MimeMessage mimeMessage) throws MailException {
            log.info("Skipping email send to {} (noop sender active)", safeAddresses(mimeMessage));
        }

        @Override
        public void send(MimeMessage... mimeMessages) throws MailException {
            if (mimeMessages != null) {
                for (MimeMessage message : mimeMessages) {
                    send(message);
                }
            }
        }

        @Override
        public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
            try {
                MimeMessage msg = createMimeMessage();
                mimeMessagePreparator.prepare(msg);
                send(msg);
            } catch (MailException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new MailPreparationException("Failed to prepare MimeMessage", ex);
            }
        }

        @Override
        public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
            if (mimeMessagePreparators != null) {
                for (MimeMessagePreparator preparator : mimeMessagePreparators) {
                    send(preparator);
                }
            }
        }

        @Override
        public void send(SimpleMailMessage simpleMessage) throws MailException {
            if (simpleMessage != null) {
                log.info("Skipping simple email send to {} with subject '{}' (noop sender active)",
                        Arrays.toString(simpleMessage.getTo()), simpleMessage.getSubject());
            } else {
                log.info("Skipping simple email send (null message) because noop sender is active");
            }
        }

        @Override
        public void send(SimpleMailMessage... simpleMessages) throws MailException {
            if (simpleMessages != null) {
                for (SimpleMailMessage message : simpleMessages) {
                    send(message);
                }
            }
        }

        private String safeAddresses(MimeMessage message) {
            try {
                var recipients = message.getAllRecipients();
                return recipients == null ? "[]" : Arrays.toString(recipients);
            } catch (Exception ex) {
                return "[unknown]";
            }
        }
    }
}
