package com.maavooripachadi.engage;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "outbound_send_log")
public class OutboundSendLog extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboundChannel channel;


    private String templateCode;


    private String target; // email, phone, or token/device


    private String status; // SENT/FAILED


    private String providerMessageId; // gateway msg id


    @Lob
    private String error; // error text if failed


    @Lob
    private String metadataJson; // rendered vars / payload snapshot


    // ---- getters/setters ----
    public OutboundChannel getChannel() { return channel; }
    public void setChannel(OutboundChannel channel) { this.channel = channel; }


    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }


    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }


    public String getProviderMessageId() { return providerMessageId; }
    public void setProviderMessageId(String providerMessageId) { this.providerMessageId = providerMessageId; }


    public String getError() { return error; }
    public void setError(String error) { this.error = error; }


    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
}