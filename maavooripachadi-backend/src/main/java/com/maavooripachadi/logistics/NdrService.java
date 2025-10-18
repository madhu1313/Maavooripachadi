package com.maavooripachadi.logistics;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NdrService {
  private final NdrTokenRepository repo;

  public String issue(String orderNo) {
    String token = UUID.randomUUID().toString().replace("-", "");
    var ndrToken = new NdrToken();
    ndrToken.setOrderNo(orderNo);
    ndrToken.setToken(token);
    ndrToken.setExpiresAt(OffsetDateTime.now().plusDays(3));
    repo.save(ndrToken);
    return token;
  }

  public boolean valid(String token) {
    return repo.findByToken(token)
        .filter(x -> !Boolean.TRUE.equals(x.getUsed()) && x.getExpiresAt().isAfter(OffsetDateTime.now()))
        .isPresent();
  }

  public void consume(String token) {
    repo.findByToken(token).ifPresent(x -> {
      x.setUsed(true);
      repo.save(x);
    });
  }
}
