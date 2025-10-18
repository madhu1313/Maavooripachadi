package com.maavooripachadi.support;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "support_sla")
public class TicketSla extends BaseEntity {
    private String name; // e.g., Default
    private int firstResponseMins = 120; // 2h
    private int resolveMins = 2880; // 48h

    // getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getFirstResponseMins() { return firstResponseMins; }
    public void setFirstResponseMins(int firstResponseMins) { this.firstResponseMins = firstResponseMins; }
    public int getResolveMins() { return resolveMins; }
    public void setResolveMins(int resolveMins) { this.resolveMins = resolveMins; }
}
