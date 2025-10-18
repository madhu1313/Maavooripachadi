package com.maavooripachadi.returns;


import com.maavooripachadi.returns.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class ReturnsService {


    private final ReturnRequestRepository requests;
    private final ReturnItemRepository items;
    private final ReturnEventRepository events;
    private final InventoryPort inventory;
    private final PaymentsPort payments;


    public ReturnsService(ReturnRequestRepository requests,
                          ReturnItemRepository items,
                          ReturnEventRepository events,
                          InventoryPort inventory,
                          PaymentsPort payments) {
        this.requests = requests; this.items = items; this.events = events; this.inventory = inventory; this.payments = payments;
    }


    @Transactional
    public ReturnRequest create(CreateReturnRequest req){
        ReturnRequest r = new ReturnRequest();
        r.setOrderNo(req.getOrderNo()); r.setCustomerEmail(req.getCustomerEmail()); r.setRefundMethod(req.getRefundMethod()); r.setNotes(req.getNotes());
        r = requests.save(r);
        for (CreateReturnItem it : req.getItems()){
            ReturnItem ri = new ReturnItem();
            ri.setRequest(r); ri.setOrderLineId(it.getOrderLineId()); ri.setVariantId(it.getVariantId()); ri.setReason(it.getReason()); ri.setQty(it.getQty());
            items.save(ri);
        }
        log(r, "OPENED", null);
        return r;
    }
@Transactional
public ReturnRequest approve(Long id, ApproveReturnRequest body){
    ReturnRequest r = requests.findById(id).orElseThrow();
    r.setStatus(ReturnStatus.APPROVED);
    r.setApprovedAt(OffsetDateTime.now());
    if (body.getRmaCode()==null || body.getRmaCode().isBlank()) r.setRmaCode("RMA-"+UUID.randomUUID().toString().substring(0,8).toUpperCase());
    else r.setRmaCode(body.getRmaCode());
    r = requests.save(r);
    log(r, "APPROVED", body.getAdminNote());
    return r;
}


@Transactional
public ReturnRequest reject(Long id, String note){
    ReturnRequest r = requests.findById(id).orElseThrow();
    r.setStatus(ReturnStatus.REJECTED);
    r.setClosedAt(OffsetDateTime.now());
    r = requests.save(r);
    log(r, "REJECTED", note);
    return r;
}


@Transactional
public ReturnItem receive(ReceiveItemsRequest body){
    ReturnItem it = items.findById(body.getReturnItemId()).orElseThrow();
    it.setReceivedQty(body.getReceivedQty()); items.save(it);
    if (body.isRestock() && body.getReceivedQty() > 0){ inventory.incrementOnHand(it.getVariantId(), body.getReceivedQty()); }
    ReturnRequest r = it.getRequest();
    int total = r.getItems().stream().mapToInt(ReturnItem::getQty).sum();
    int recvd = r.getItems().stream().mapToInt(ReturnItem::getReceivedQty).sum();
    if (recvd == 0) { /* no change */ }
    else if (recvd < total) { r.setStatus(ReturnStatus.PARTIAL_RECEIVED); r.setReceivedAt(OffsetDateTime.now()); requests.save(r); }
    else { r.setStatus(ReturnStatus.RECEIVED); r.setReceivedAt(OffsetDateTime.now()); requests.save(r); }
    log(r, "RECEIVED", body.getNote());
    return it;
}


@Transactional
public ReturnRequest refund(Long id, RefundDecisionRequest body){
    ReturnRequest r = requests.findById(id).orElseThrow();
    String ref = payments.refund(r.getOrderNo(), body.getRefundPaise(), body.getReason());
    r.setStatus(ReturnStatus.REFUNDED); r.setClosedAt(OffsetDateTime.now());
    r = requests.save(r);
    log(r, "REFUNDED", "ref="+ref+",reason="+body.getReason());
    return r;
}


@Transactional
public ReturnRequest exchange(Long id, ExchangeDecisionRequest body){
    ReturnRequest r = requests.findById(id).orElseThrow();
    r.setStatus(ReturnStatus.EXCHANGED); r.setClosedAt(OffsetDateTime.now());
    r = requests.save(r);
    log(r, "EXCHANGED", body.getNote());
    return r;
}


private void log(ReturnRequest r, String kind, String note){
    ReturnEvent e = new ReturnEvent(); e.setRequest(r); e.setKind(kind); e.setPayloadJson(note==null?null:("{\"note\":\""+note.replace("\"","'")+"\"}")); events.save(e);
}
}