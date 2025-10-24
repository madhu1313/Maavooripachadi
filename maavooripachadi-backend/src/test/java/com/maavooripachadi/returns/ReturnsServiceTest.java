package com.maavooripachadi.returns;

import com.maavooripachadi.returns.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReturnsServiceTest {

    @Mock private ReturnRequestRepository requestRepository;
    @Mock private ReturnItemRepository itemRepository;
    @Mock private ReturnEventRepository eventRepository;
    @Mock private InventoryPort inventoryPort;
    @Mock private PaymentsPort paymentsPort;

    private ReturnsService service;

    @BeforeEach
    void setUp() {
        service = new ReturnsService(requestRepository, itemRepository, eventRepository, inventoryPort, paymentsPort);
    }

    @Test
    void createPersistsRequestItemsAndLogsOpenedEvent() {
        CreateReturnItem item1 = new CreateReturnItem();
        item1.setOrderLineId(1L);
        item1.setVariantId(101L);
        item1.setReason(ReturnReason.WRONG_ITEM);
        item1.setQty(2);

        CreateReturnItem item2 = new CreateReturnItem();
        item2.setOrderLineId(2L);
        item2.setVariantId(102L);
        item2.setReason(ReturnReason.DAMAGE);
        item2.setQty(1);

        CreateReturnRequest request = new CreateReturnRequest();
        request.setOrderNo("ORD-1");
        request.setCustomerEmail("user@example.com");
        request.setItems(List.of(item1, item2));
        request.setNotes("Please help");

        when(requestRepository.save(any(ReturnRequest.class))).thenAnswer(invocation -> {
            ReturnRequest entity = invocation.getArgument(0);
            ReflectionTestUtils.setField(entity, "id", 10L);
            return entity;
        });
        when(itemRepository.save(any(ReturnItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReturnRequest result = service.create(request);

        assertThat(result.getOrderNo()).isEqualTo("ORD-1");

        ArgumentCaptor<ReturnItem> itemCaptor = ArgumentCaptor.forClass(ReturnItem.class);
        verify(itemRepository, times(2)).save(itemCaptor.capture());
        assertThat(itemCaptor.getAllValues()).extracting(ReturnItem::getVariantId).containsExactlyInAnyOrder(101L, 102L);

        ArgumentCaptor<ReturnEvent> eventCaptor = ArgumentCaptor.forClass(ReturnEvent.class);
        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getKind()).isEqualTo("OPENED");
    }

    @Test
    void approveUpdatesRequestAndGeneratesRmaCode() {
        ReturnRequest stored = new ReturnRequest();
        stored.setOrderNo("ORD-2");
        when(requestRepository.findById(5L)).thenReturn(Optional.of(stored));
        when(requestRepository.save(stored)).thenReturn(stored);

        ApproveReturnRequest body = new ApproveReturnRequest();
        body.setAdminNote("Looks good");

        ReturnRequest result = service.approve(5L, body);

        assertThat(result.getStatus()).isEqualTo(ReturnStatus.APPROVED);
        assertThat(result.getApprovedAt()).isNotNull();
        assertThat(result.getRmaCode()).startsWith("RMA-");
        verify(eventRepository).save(any(ReturnEvent.class));
    }

    @Test
    void receiveUpdatesReceivedQtyTriggersRestockAndPartialStatus() {
        ReturnRequest request = new ReturnRequest();
        ReturnItem item = new ReturnItem();
        item.setRequest(request);
        item.setQty(2);
        request.getItems().add(item);
        when(itemRepository.findById(3L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(requestRepository.save(request)).thenReturn(request);

        ReceiveItemsRequest body = new ReceiveItemsRequest();
        body.setReturnItemId(3L);
        body.setReceivedQty(1);
        body.setRestock(true);
        body.setNote("Partial receipt");

        ReturnItem updated = service.receive(body);

        assertThat(updated.getReceivedQty()).isEqualTo(1);
        assertThat(request.getStatus()).isEqualTo(ReturnStatus.PARTIAL_RECEIVED);
        verify(inventoryPort).incrementOnHand(item.getVariantId(), 1);
        verify(eventRepository).save(any(ReturnEvent.class));
    }

    @Test
    void refundInvokesPaymentsAndClosesRequest() {
        ReturnRequest request = new ReturnRequest();
        request.setOrderNo("ORD-3");
        when(requestRepository.findById(7L)).thenReturn(Optional.of(request));
        when(requestRepository.save(request)).thenReturn(request);
        when(paymentsPort.refund("ORD-3", 5000, "Damaged")).thenReturn("REF-123");

        RefundDecisionRequest body = new RefundDecisionRequest();
        body.setOrderNo("ORD-3");
        body.setRefundPaise(5000);
        body.setReason("Damaged");

        ReturnRequest result = service.refund(7L, body);

        assertThat(result.getStatus()).isEqualTo(ReturnStatus.REFUNDED);
        assertThat(result.getClosedAt()).isNotNull();
        verify(paymentsPort).refund("ORD-3", 5000, "Damaged");
        verify(eventRepository).save(any(ReturnEvent.class));
    }

    @Test
    void exchangeMarksRequestExchanged() {
        ReturnRequest request = new ReturnRequest();
        when(requestRepository.findById(8L)).thenReturn(Optional.of(request));
        when(requestRepository.save(request)).thenReturn(request);

        ExchangeDecisionRequest body = new ExchangeDecisionRequest();
        body.setNote("Sent replacement");

        ReturnRequest result = service.exchange(8L, body);

        assertThat(result.getStatus()).isEqualTo(ReturnStatus.EXCHANGED);
        assertThat(result.getClosedAt()).isNotNull();
        verify(eventRepository).save(any(ReturnEvent.class));
    }
}
