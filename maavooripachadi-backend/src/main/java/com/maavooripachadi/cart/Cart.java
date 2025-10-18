package com.maavooripachadi.cart;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "cart")
public class Cart extends BaseEntity {
  @Column(unique = true, nullable = false)
  private String sessionId;


  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<CartItem> items = new ArrayList<>();


  public Cart() {
  }


  public String getSessionId() {
    return sessionId;
  }


  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }


  public List<CartItem> getItems() {
    return items;
  }


  public void setItems(List<CartItem> items) {
    this.items = items;
  }
}
