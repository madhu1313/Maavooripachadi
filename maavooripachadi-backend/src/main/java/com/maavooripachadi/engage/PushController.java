package com.maavooripachadi.engage;


import com.maavooripachadi.engage.dto.RegisterPushTokenRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/push")
@Validated
public class PushController {
  private final PushService push;


  public PushController(PushService push){ this.push = push; }


  @PostMapping("/register")
  public PushToken register(@Valid @RequestBody RegisterPushTokenRequest req){
    return push.register(req);
  }


  @PostMapping("/disable")
  public java.util.Map<String,Object> disable(@RequestParam @NotBlank String token){
    push.disableByToken(token);
    return java.util.Map.of("ok", true);
  }


  @GetMapping("/tokens")
  public java.util.List<PushToken> list(){ return push.list(); }
}