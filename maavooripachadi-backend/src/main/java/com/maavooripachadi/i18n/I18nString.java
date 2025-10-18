package com.maavooripachadi.i18n;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "i18n_string",
        uniqueConstraints = @UniqueConstraint(name = "uk_i18n_ns_key_locale", columnNames = {"namespace", "ikey", "locale"}))
public class I18nString extends BaseEntity {


  @Column(nullable = false, length = 64)
  private String namespace; // e.g., "storefront", "checkout"


  @Column(name = "ikey", nullable = false, length = 128)
  private String key; // e.g., "hero.title"


  @Column(nullable = false, length = 16)
  private String locale; // e.g., "en", "en-IN", "hi"


  @Lob
  private String text; // translated value


  @Column(length = 256)
  private String tags; // comma separated, optional


  @Column(length = 64)
  private String checksum; // optional integrity/version tracking


  private Boolean approved = Boolean.TRUE; // moderation flag


  // ---- getters/setters ----
  public String getNamespace() { return namespace; }
  public void setNamespace(String namespace) { this.namespace = namespace; }


  public String getKey() { return key; }
  public void setKey(String key) { this.key = key; }


  public String getLocale() { return locale; }
  public void setLocale(String locale) { this.locale = locale; }


  public String getText() { return text; }
  public void setText(String text) { this.text = text; }


  public String getTags() { return tags; }
  public void setTags(String tags) { this.tags = tags; }


  public String getChecksum() { return checksum; }
  public void setChecksum(String checksum) { this.checksum = checksum; }


  public Boolean getApproved() { return approved; }
  public void setApproved(Boolean approved) { this.approved = approved; }
}