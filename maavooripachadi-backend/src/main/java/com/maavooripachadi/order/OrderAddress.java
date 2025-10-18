package com.maavooripachadi.order;


import jakarta.persistence.Embeddable;


@Embeddable
public class OrderAddress {
    private String name; private String phone; private String line1; private String line2; private String city; private String state; private String pincode; private String country = "IN";
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getLine1() { return line1; } public void setLine1(String line1) { this.line1 = line1; }
    public String getLine2() { return line2; } public void setLine2(String line2) { this.line2 = line2; }
    public String getCity() { return city; } public void setCity(String city) { this.city = city; }
    public String getState() { return state; } public void setState(String state) { this.state = state; }
    public String getPincode() { return pincode; } public void setPincode(String pincode) { this.pincode = pincode; }
    public String getCountry() { return country; } public void setCountry(String country) { this.country = country; }
}