package com.maavooripachadi.risk;

import com.maavooripachadi.risk.dto.EvaluateRequest;
import com.maavooripachadi.risk.dto.EvaluateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskEngineServiceTest {

    @Mock private DenyListService denyListService;
    @Mock private VelocityService velocityService;
    @Mock private RuleEngine ruleEngine;
    @Mock private RiskEventRepository eventRepository;
    @Mock private RiskScoreRepository scoreRepository;
    @Mock private RiskCaseRepository caseRepository;

    private RiskEngineService service;

    @BeforeEach
    void setUp() {
        service = new RiskEngineService(denyListService, velocityService, ruleEngine, eventRepository, scoreRepository, caseRepository);
    }

    @Test
    void evaluateCreatesBlockDecisionWhenDenied() {
        EvaluateRequest request = new EvaluateRequest();
        request.setEmail("fraud@example.com");
        request.setAmountPaise(75_000);

        when(eventRepository.save(any(RiskEvent.class))).thenAnswer(invocation -> {
            RiskEvent event = invocation.getArgument(0);
            ReflectionTestUtils.setField(event, "id", 101L);
            return event;
        });
        when(denyListService.isDenied(DenyType.EMAIL, "fraud@example.com")).thenReturn(true);
        when(ruleEngine.eval(any(EvaluateRequest.class), anyString())).thenReturn(new RuleEngine.Result());
        when(scoreRepository.save(any(RiskScore.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(caseRepository.save(any(RiskCase.class))).thenAnswer(invocation -> {
            RiskCase saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 200L);
            return saved;
        });

        EvaluateResponse response = service.evaluate(request);

        assertThat(response.getDecision()).isEqualTo(RiskDecision.BLOCK);
        assertThat(response.getCaseId()).isEqualTo(200L);
        assertThat(response.getReasons()).contains("deny:email");
    }

    @Test
    void evaluateAllowsWhenNoSignalsTrigger() {
        EvaluateRequest request = new EvaluateRequest();
        request.setEmail("ok@example.com");
        request.setAmountPaise(5_000);

        when(eventRepository.save(any(RiskEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ruleEngine.eval(any(EvaluateRequest.class), anyString())).thenReturn(new RuleEngine.Result());
        when(scoreRepository.save(any(RiskScore.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EvaluateResponse response = service.evaluate(request);

        assertThat(response.getDecision()).isEqualTo(RiskDecision.ALLOW);
        assertThat(response.getCaseId()).isNull();
        assertThat(response.getReasons()).isEmpty();
        verify(caseRepository, never()).save(any());
    }
}
