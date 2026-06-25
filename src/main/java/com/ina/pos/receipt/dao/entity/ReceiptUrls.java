package com.ina.pos.receipt.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "receipt_urls")
public class ReceiptUrls {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "reference_id", nullable = false)
    private String referenceId;
    @Column(name = "receipt_number", nullable = false)
    private String receiptNumber;
    @Column(name = "total", nullable = false)
    private String total;
    @Column(name = "currency", nullable = false)
    private String currency;
    @Column(name = "store_name", nullable = false)
    private String storeName;
    @Column(name = "url", nullable = false)
    private String url;
    @Column(name = "created_date",nullable = false)
    private Timestamp createdDate;
    @Column(name = "updated_date",nullable = false)
    private Timestamp updatedDate;
}
