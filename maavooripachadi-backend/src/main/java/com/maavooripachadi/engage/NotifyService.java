package com.maavooripachadi.engage;


import com.maavooripachadi.engage.dto.SendRequest;
import com.maavooripachadi.engage.dto.PreviewRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.util.Map;

@Service
public class NotifyService {
    private final OutboundTemplateRepository templates;
    private final OutboundSendLogRepository logs;
    private final JavaMailSender mailSender;
    private final TemplateRenderer renderer = new TemplateRenderer();


    public NotifyService(OutboundTemplateRepository templates,
                         OutboundSendLogRepository logs,
                         JavaMailSender mailSender) {
        this.templates = templates; this.logs = logs; this.mailSender = mailSender;
    }


    public Map<String, Object> preview(PreviewRequest req){
        var t = templates.findByCode(req.templateCode()).orElseThrow();
        var subject = renderer.render(t.getSubject(), req.variables());
        var bodyHtml = renderer.render(t.getBodyHtml(), req.variables());
        var bodyText = renderer.render(t.getBodyText(), req.variables());
        return Map.of("subject", subject, "bodyHtml", bodyHtml, "bodyText", bodyText);
    }


    @Transactional
    public Long send(SendRequest req){
var t = templates.findByCode(req.templateCode()).orElseThrow();
if (t.getEnabled() == null || !t.getEnabled()) throw new IllegalStateException("Template disabled");
OutboundChannel ch = req.channel();
String renderedSubject = renderer.render(t.getSubject(), req.variables());
String renderedHtml = renderer.render(t.getBodyHtml(), req.variables());
String renderedText = renderer.render(t.getBodyText(), req.variables());


String providerId = null; String status = "SENT"; String error = null;
try {
        switch (ch){
        case EMAIL -> providerId = sendEmail(req.target(), renderedSubject, renderedText, renderedHtml);
        case SMS -> providerId = sendSms(req.target(), renderedText);
        case PUSH -> providerId = sendPush(req.target(), renderedText);
        }
        } catch (Exception ex){
status = "FAILED"; error = ex.getMessage();
}


var log = new OutboundSendLog();
log.setChannel(ch);
log.setTemplateCode(req.templateCode());
        log.setTarget(req.target());
        log.setStatus(status);
log.setProviderMessageId(providerId);
log.setError(error);
log.setMetadataJson("{}"); // place to stash variables snapshot if needed
logs.save(log);


if (!"SENT".equals(status)) throw new IllegalStateException("Send failed: " + error);
return log.getId();
}


private String sendEmail(String to, String subject, String text, String html){
// Minimal text email. For HTML, use MimeMessageHelper.
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo(to);
    msg.setSubject(subject == null ? "" : subject);
    msg.setText((text != null && !text.isBlank()) ? text : (html == null ? "" : html.replaceAll("<[^>]+>", "")));
    mailSender.send(msg);
    return "mail-" + OffsetDateTime.now().toEpochSecond();
}


private String sendSms(String phone, String text){
// TODO integrate with SMS provider (e.g., Gupshup/Twilio). For now, stub an id.
    if (text == null || text.isBlank()) throw new IllegalArgumentException("SMS text required");
    return "sms-" + OffsetDateTime.now().toEpochSecond();
}


private String sendPush(String tokenOrDevice, String text){
// TODO integrate with FCM/APNs. For now, require a token/device id.
    if (tokenOrDevice == null || tokenOrDevice.isBlank()) throw new IllegalArgumentException("push token required");
    return "push-" + OffsetDateTime.now().toEpochSecond();
}
}
