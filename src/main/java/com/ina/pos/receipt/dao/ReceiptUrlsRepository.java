package com.ina.pos.receipt.dao;

import com.ina.pos.receipt.dao.entity.ReceiptUrls;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptUrlsRepository extends JpaRepository<ReceiptUrls,Long> {
    ReceiptUrls findByReferenceId(String referenceId);
}
