package com.maavooripachadi.risk;

import com.maavooripachadi.risk.dto.EvaluateRequest;
import com.maavooripachadi.risk.dto.EvaluateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskPublicControllerTest {

    @Mock private RiskEngineService engineService;
    private RiskPublicController controller;

    @BeforeEach
    void setUp() {
        controller = new RiskPublicController(engineService);
    }

    @Test
    void evaluateDelegatesToEngine() {
        EvaluateRequest request = new EvaluateRequest();
        EvaluateResponse response = new EvaluateResponse();
        response.setDecision(RiskDecision.ALLOW);
        when(engineService.evaluate(request)).thenReturn(response);

        EvaluateResponse result = controller.evaluate(request);

        assertThat(result).isSameAs(response);
        verify(engineService).evaluate(request);
    }
}
