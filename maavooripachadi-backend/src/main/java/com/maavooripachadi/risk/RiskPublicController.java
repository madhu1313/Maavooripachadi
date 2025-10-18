package com.maavooripachadi.risk;

import com.maavooripachadi.risk.dto.EvaluateRequest;
import com.maavooripachadi.risk.dto.EvaluateResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/risk")
public class RiskPublicController {
    private final RiskEngineService engine;
    public RiskPublicController(RiskEngineService engine){ this.engine = engine; }

    @PostMapping("/evaluate")
    public EvaluateResponse evaluate(@Valid @RequestBody EvaluateRequest req){
        return engine.evaluate(req);
    }
}
