package com.maavooripachadi.payments.recovery;

import com.maavooripachadi.payments.recovery.dto.RecoveryIssueRequest;
import com.maavooripachadi.payments.recovery.dto.RecoveryIssueResponse;
import com.maavooripachadi.payments.recovery.dto.RecoveryValidateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecoveryControllerTest {

    @Mock
    private RecoveryService service;

    private RecoveryController controller;

    @BeforeEach
    void setUp() {
        controller = new RecoveryController(service);
    }

    @Test
    void issueDelegatesToService() {
        RecoveryIssueRequest request = new RecoveryIssueRequest();
        RecoveryIssueResponse response = new RecoveryIssueResponse();
        response.setToken("tok");
        when(service.issue(request)).thenReturn(response);

        RecoveryIssueResponse result = controller.issue(request);

        assertThat(result).isSameAs(response);
        verify(service).issue(request);
    }

    @Test
    void validateDelegatesToService() {
        RecoveryValidateResponse response = new RecoveryValidateResponse();
        response.setOk(true);
        when(service.validate("token")).thenReturn(response);

        RecoveryValidateResponse result = controller.validate("token");

        assertThat(result).isSameAs(response);
        verify(service).validate("token");
    }

    @Test
    void consumeReturnsOkMap() {
        Map<String, Object> result = controller.consume("token");

        assertThat(result).containsEntry("ok", true);
        verify(service).consume("token");
    }
}
