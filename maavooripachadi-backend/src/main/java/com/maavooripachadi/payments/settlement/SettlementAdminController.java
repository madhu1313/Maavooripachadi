package com.maavooripachadi.payments.settlement;


import com.maavooripachadi.payments.settlement.dto.BatchSummaryResponse;
import com.maavooripachadi.payments.settlement.dto.SettlementIngestRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/v1/admin/settlement")
public class SettlementAdminController {


    private final SettlementService service;


    public SettlementAdminController(SettlementService service){ this.service = service; }


    @PostMapping("/ingest")
    @PreAuthorize("hasAuthority('PAYMENT_WRITE') or hasRole('ADMIN')")
    public SettlementBatch ingest(@Valid @RequestBody SettlementIngestRequest req){
        return service.ingest(req);
    }


    @GetMapping
    @PreAuthorize("hasAuthority('PAYMENT_READ') or hasRole('ADMIN')")
    public List<BatchSummaryResponse> list(){
        return service.list();
    }
}