package com.maavooripachadi.compliance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountingSyncService {
  private final AccountingSyncRepository repo;

  @Transactional
  public AccountingSync queue(String type, String refId, String system) {
    var sync = new AccountingSync();
    sync.setType(type);
    sync.setRefId(refId);
    sync.setSystem(system);
    sync.setStatus("QUEUED");
    return repo.save(sync);
  }
}
