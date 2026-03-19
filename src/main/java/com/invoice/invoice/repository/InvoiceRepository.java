package com.invoice.invoice.repository;

import com.invoice.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByClientNameContainingIgnoreCase(String clientName);
    List<Invoice> findByStatus(String status);
    List<Invoice> findByInvoiceNumberContainingIgnoreCase(String invoiceNumber);
}
