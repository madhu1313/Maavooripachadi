package com.maavooripachadi.payments.settlement;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "settlement_reconcile_file")
public class ReconcileFile extends BaseEntity {


    @Column(length = 64)
    private String fileId; // internal id (e.g., cloud key)


    private String originalName;


    @Lob
    private String storageUrl; // where file is stored


    @Column(length = 16)
    private String gateway; // RAZORPAY/CASHFREE


    @Column(length = 64)
    private String ingestedBy; // admin user id/email


    // getters/setters
    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getStorageUrl() { return storageUrl; }
    public void setStorageUrl(String storageUrl) { this.storageUrl = storageUrl; }
    public String getGateway() { return gateway; }
    public void setGateway(String gateway) { this.gateway = gateway; }
    public String getIngestedBy() { return ingestedBy; }
    public void setIngestedBy(String ingestedBy) { this.ingestedBy = ingestedBy; }
}