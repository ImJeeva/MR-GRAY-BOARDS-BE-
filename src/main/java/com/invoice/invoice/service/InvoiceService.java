package com.invoice.invoice.service;

import com.invoice.invoice.entity.Client;
import com.invoice.invoice.entity.Company;
import com.invoice.invoice.entity.Invoice;
import com.invoice.invoice.entity.InvoiceItem;
import com.invoice.invoice.repository.ClientRepository;
import com.invoice.invoice.repository.CompanyRepository;
import com.invoice.invoice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final CompanyRepository companyRepository;
    
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
    
    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }
    
    @Transactional
    public Invoice createInvoice(Invoice invoice, Long clientId, Long companyId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        
        invoice.setClient(client);
        invoice.setCompany(company);
        
        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isEmpty()) {
            invoice.setInvoiceNumber(generateInvoiceNumber());
        }
        
        if (invoice.getInvoiceDate() == null) {
            invoice.setInvoiceDate(LocalDate.now());
        }
        
        if (invoice.getStatus() == null) {
            invoice.setStatus("PENDING");
        }
        
        if (invoice.getItems() != null) {
            for (InvoiceItem item : invoice.getItems()) {
                item.setInvoice(invoice);
            }
        }
        
        calculateTotals(invoice);
        
        return invoiceRepository.save(invoice);
    }
    
    @Transactional
    public Invoice updateInvoice(Long id, Invoice invoice) {
        Invoice existing = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        existing.setInvoiceNumber(invoice.getInvoiceNumber());
        existing.setInvoiceDate(invoice.getInvoiceDate());
        existing.setDueDate(invoice.getDueDate());
        existing.setStatus(invoice.getStatus());
        existing.setNotes(invoice.getNotes());
        existing.setTransportMode(invoice.getTransportMode());
        existing.setVehicleNumber(invoice.getVehicleNumber());
        existing.setPlaceOfSupply(invoice.getPlaceOfSupply());
        existing.setReverseCharge(invoice.getReverseCharge());
        existing.setDateOfSupply(invoice.getDateOfSupply());
        
        if (invoice.getItems() != null) {
            existing.getItems().clear();
            existing.getItems().addAll(invoice.getItems());
            for (InvoiceItem item : existing.getItems()) {
                item.setInvoice(existing);
            }
        }
        
        calculateTotals(existing);
        
        return invoiceRepository.save(existing);
    }
    
    @Transactional
    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }
    
    public List<Invoice> searchInvoices(String query) {
        return invoiceRepository.findByInvoiceNumberContainingIgnoreCase(query);
    }
    
    public List<Invoice> getInvoicesByStatus(String status) {
        return invoiceRepository.findByStatus(status);
    }
    
    private String generateInvoiceNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = invoiceRepository.count() + 1;
        return "INV-" + date + "-" + String.format("%04d", count);
    }
    
    private void calculateTotals(Invoice invoice) {
        double subtotal = 0;
        double cgstAmount = 0;
        double sgstAmount = 0;
        double igstAmount = 0;
        
        if (invoice.getItems() != null) {
            for (InvoiceItem item : invoice.getItems()) {
                double amount = item.getQuantity() * item.getUnitPrice();
                item.setAmount(amount);
                item.setTaxableValue(amount);
                subtotal += amount;
                
                double cgst = item.getCgstRate() != null ? item.getCgstRate() : 0;
                double sgst = item.getSgstRate() != null ? item.getSgstRate() : 0;
                double igst = item.getIgstRate() != null ? item.getIgstRate() : 0;
                
                double cgstAmt = amount * (cgst / 100);
                double sgstAmt = amount * (sgst / 100);
                double igstAmt = amount * (igst / 100);
                
                item.setCgstAmount(cgstAmt);
                item.setSgstAmount(sgstAmt);
                item.setIgstAmount(igstAmt);
                
                cgstAmount += cgstAmt;
                sgstAmount += sgstAmt;
                igstAmount += igstAmt;
            }
        }
        
        invoice.setSubtotal(subtotal);
        invoice.setCgstAmount(cgstAmount);
        invoice.setSgstAmount(sgstAmount);
        invoice.setIgstAmount(igstAmount);
        invoice.setTotalTax(cgstAmount + sgstAmount + igstAmount);
        
        invoice.setTotal(subtotal + cgstAmount + sgstAmount + igstAmount);
    }
}
