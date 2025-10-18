package com.maavooripachadi.payments.settlement;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "settlement_batch")
public class SettlementBatch extends BaseEntity {


    @Column(nullable = false, length = 16)
    private String gateway; // RAZORPAY/CASHFREE


    @Column(nullable = false)
    private LocalDate payoutDate; // Banking payout date


    @Column(nullable = false)
    private int totalAmountPaise;


    @Column(nullable = false)
    private int countTxns;


    @Column(length = 64)
    private String fileId; // ID of original file


    @Column(length = 64)
    private String checksum;


    // getters/setters
    public String getGateway() { return gateway; }
    public void setGateway(String gateway) { this.gateway = gateway; }
    public LocalDate getPayoutDate() { return payoutDate; }
    public void setPayoutDate(LocalDate payoutDate) { this.payoutDate = payoutDate; }
    public int getTotalAmountPaise() { return totalAmountPaise; }
    public void setTotalAmountPaise(int totalAmountPaise) { this.totalAmountPaise = totalAmountPaise; }
    public int getCountTxns() { return countTxns; }
    public void setCountTxns(int countTxns) { this.countTxns = countTxns; }
    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
}