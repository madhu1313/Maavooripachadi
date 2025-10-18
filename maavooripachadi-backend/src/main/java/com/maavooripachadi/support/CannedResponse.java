package com.maavooripachadi.support;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "support_canned", indexes = @Index(name = "ix_canned_key", columnList = "key_name", unique = true))
public class CannedResponse extends BaseEntity {
    @Column(name = "key_name", nullable = false, unique = true)
    private String keyName; // e.g., shipping_delay

    @Lob
    private String body;

    private String locale = "en-IN";

    // getters & setters
    public String getKeyName() { return keyName; }
    public void setKeyName(String keyName) { this.keyName = keyName; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
}
