package com.maavooripachadi.order;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "order_note")
public class OrderNote extends BaseEntity {
    @ManyToOne(optional = false)
    private Order order;
    @Lob
    private String note;
    private String author;


    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}