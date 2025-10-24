package com.maavooripachadi.returns;

import com.maavooripachadi.returns.dto.ApproveReturnRequest;
import com.maavooripachadi.returns.dto.ExchangeDecisionRequest;
import com.maavooripachadi.returns.dto.ReceiveItemsRequest;
import com.maavooripachadi.returns.dto.RefundDecisionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnsAdminControllerTest {

    @Mock private ReturnsService returnsService;

    private ReturnsAdminController controller;

    @BeforeEach
    void setUp() {
        controller = new ReturnsAdminController(returnsService);
    }

    @Test
    void approveDelegatesToService() {
        ApproveReturnRequest body = new ApproveReturnRequest();
        ReturnRequest response = new ReturnRequest();
        when(returnsService.approve(1L, body)).thenReturn(response);

        assertThat(controller.approve(1L, body)).isSameAs(response);
        verify(returnsService).approve(1L, body);
    }

    @Test
    void rejectDelegatesToService() {
        ReturnRequest response = new ReturnRequest();
        when(returnsService.reject(2L, "nope")).thenReturn(response);

        assertThat(controller.reject(2L, "nope")).isSameAs(response);
        verify(returnsService).reject(2L, "nope");
    }

    @Test
    void receiveDelegatesToService() {
        ReceiveItemsRequest body = new ReceiveItemsRequest();
        ReturnItem item = new ReturnItem();
        when(returnsService.receive(body)).thenReturn(item);

        assertThat(controller.receive(body)).isSameAs(item);
        verify(returnsService).receive(body);
    }

    @Test
    void refundDelegatesToService() {
        RefundDecisionRequest body = new RefundDecisionRequest();
        ReturnRequest response = new ReturnRequest();
        when(returnsService.refund(3L, body)).thenReturn(response);

        assertThat(controller.refund(3L, body)).isSameAs(response);
        verify(returnsService).refund(3L, body);
    }

    @Test
    void exchangeDelegatesToService() {
        ExchangeDecisionRequest body = new ExchangeDecisionRequest();
        ReturnRequest response = new ReturnRequest();
        when(returnsService.exchange(4L, body)).thenReturn(response);

        assertThat(controller.exchange(4L, body)).isSameAs(response);
        verify(returnsService).exchange(4L, body);
    }
}
