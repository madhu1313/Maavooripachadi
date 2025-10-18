package com.maavooripachadi.logistics;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "warehouse")
public class Warehouse extends BaseEntity {
  @Column(unique = true, nullable = false)
  private String code;


  @Column(nullable = false)
  private String name;


  private String line1;
  private String line2;
  private String city;
  private String state;
  private String pincode;
  private String country;


  private Boolean active = Boolean.TRUE;


  // getters/setters
  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getLine1() { return line1; }
  public void setLine1(String line1) { this.line1 = line1; }
  public String getLine2() { return line2; }
  public void setLine2(String line2) { this.line2 = line2; }
  public String getCity() { return city; }
  public void setCity(String city) { this.city = city; }
  public String getState() { return state; }
  public void setState(String state) { this.state = state; }
  public String getPincode() { return pincode; }
  public void setPincode(String pincode) { this.pincode = pincode; }
  public String getCountry() { return country; }
  public void setCountry(String country) { this.country = country; }
  public Boolean getActive() { return active; }
  public void setActive(Boolean active) { this.active = active; }
}