package com.maavooripachadi.payments.recovery;


import com.maavooripachadi.payments.recovery.dto.RecoveryIssueRequest;
import com.maavooripachadi.payments.recovery.dto.RecoveryIssueResponse;
import com.maavooripachadi.payments.recovery.dto.RecoveryValidateResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;


@Service
public class RecoveryService {

  private final RecoveryTokenRepository repo;
private final RecoveryProperties props;


public RecoveryService(RecoveryTokenRepository repo, RecoveryProperties props){
  this.repo = repo; this.props = props;
}


@Transactional
public RecoveryIssueResponse issue(RecoveryIssueRequest req){
  RecoveryToken t = new RecoveryToken();
  t.setOrderNo(req.getOrderNo());
  t.setToken(UUID.randomUUID().toString().replace("-", ""));
  t.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusHours(props.getTtlHours()));
  t.setConsumed(Boolean.FALSE);
  repo.save(t);


  String url = buildLink(t.getToken());
  RecoveryIssueResponse r = new RecoveryIssueResponse();
  r.setToken(t.getToken());
  r.setUrl(url);
  return r;
}


@Transactional(readOnly = true)
public RecoveryValidateResponse validate(String token){
  RecoveryValidateResponse resp = new RecoveryValidateResponse();
  Optional<RecoveryToken> opt = repo.findByTokenAndConsumedFalse(token)
          .filter(rt -> rt.getExpiresAt() == null || rt.getExpiresAt().isAfter(OffsetDateTime.now(ZoneOffset.UTC)));
  resp.setOk(opt.isPresent());
  resp.setOrderNo(opt.map(RecoveryToken::getOrderNo).orElse(null));
  return resp;
}


@Transactional
public void consume(String token){
  RecoveryToken rt = repo.findByTokenAndConsumedFalse(token).orElseThrow();
  rt.setConsumed(Boolean.TRUE);
  repo.save(rt);
}


private String buildLink(String token){
  String base = props.getLinkBase();
  if (base == null || base.isBlank()) base = "/";
  String t = URLEncoder.encode(token, StandardCharsets.UTF_8);
  if (base.endsWith("/")) return base + "pay/recover?token=" + t;
  return base + "/pay/recover?token=" + t;
}
}