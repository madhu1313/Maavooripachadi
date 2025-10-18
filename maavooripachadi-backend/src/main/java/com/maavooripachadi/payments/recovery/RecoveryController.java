package com.maavooripachadi.payments.recovery;


import com.maavooripachadi.payments.recovery.dto.RecoveryIssueRequest;
import com.maavooripachadi.payments.recovery.dto.RecoveryIssueResponse;
import com.maavooripachadi.payments.recovery.dto.RecoveryValidateResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.Map;


@RestController
@RequestMapping("/api/v1/payments/recovery")
@Validated
public class RecoveryController {


  private final RecoveryService service;


  public RecoveryController(RecoveryService service){ this.service = service; }


  @PostMapping("/issue")
  @ResponseStatus(HttpStatus.CREATED)
  public RecoveryIssueResponse issue(@Valid @RequestBody RecoveryIssueRequest req){
    return service.issue(req);
  }


  @GetMapping("/validate")
  public RecoveryValidateResponse validate(@RequestParam String token){
    return service.validate(token);
  }


  @PostMapping("/consume")
  public Map<String,Object> consume(@RequestParam String token){
    service.consume(token);
    return Map.of("ok", true);
  }
}