package com.maavooripachadi.returns;


import com.maavooripachadi.returns.dto.CreateReturnRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/returns")
@Validated
public class ReturnsPublicController {
    private final ReturnsService service;
    public ReturnsPublicController(ReturnsService service){ this.service = service; }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReturnRequest open(@Valid @RequestBody CreateReturnRequest req){ return service.create(req); }
}