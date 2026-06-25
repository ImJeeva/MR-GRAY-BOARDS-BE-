package com.invoice.invoice.controller;

import com.invoice.invoice.entity.Invoice;
import com.invoice.invoice.entity.InvoiceItem;
import com.invoice.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;
    
    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Map<String, Object> request) {
        Invoice invoice = new Invoice();
        
        if (request.containsKey("invoiceNumber")) {
            invoice.setInvoiceNumber((String) request.get("invoiceNumber"));
        }
        if (request.containsKey("invoiceDate") && request.get("invoiceDate") != null && !((String) request.get("invoiceDate")).isEmpty()) {
            invoice.setInvoiceDate(java.time.LocalDate.parse((String) request.get("invoiceDate")));
        }
        if (request.containsKey("dueDate") && request.get("dueDate") != null && !((String) request.get("dueDate")).isEmpty()) {
            invoice.setDueDate(java.time.LocalDate.parse((String) request.get("dueDate")));
        }
        if (request.containsKey("status")) {
            invoice.setStatus((String) request.get("status"));
        }
        if (request.containsKey("notes")) {
            invoice.setNotes((String) request.get("notes"));
        }
        if (request.containsKey("transportMode")) {
            invoice.setTransportMode((String) request.get("transportMode"));
        }
        if (request.containsKey("vehicleNumber")) {
            invoice.setVehicleNumber((String) request.get("vehicleNumber"));
        }
        if (request.containsKey("placeOfSupply")) {
            invoice.setPlaceOfSupply((String) request.get("placeOfSupply"));
        }
        if (request.containsKey("reverseCharge")) {
            invoice.setReverseCharge((String) request.get("reverseCharge"));
        }
        if (request.containsKey("dateOfSupply")) {
            invoice.setDateOfSupply((String) request.get("dateOfSupply"));
        }
        
        if (request.containsKey("items")) {
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) request.get("items");
            List<InvoiceItem> items = new ArrayList<>();
            for (Map<String, Object> itemData : itemsData) {
                InvoiceItem item = new InvoiceItem();
                item.setDescription((String) itemData.get("description"));
                if (itemData.containsKey("hsnCode")) {
                    item.setHsnCode((String) itemData.get("hsnCode"));
                }
                item.setQuantity(((Number) itemData.get("quantity")).intValue());
                item.setUnitPrice(((Number) itemData.get("unitPrice")).doubleValue());
                item.setAmount(((Number) itemData.get("amount")).doubleValue());
                
                if (itemData.containsKey("cgstRate") && itemData.get("cgstRate") != null) {
                    item.setCgstRate(((Number) itemData.get("cgstRate")).doubleValue());
                }
                if (itemData.containsKey("sgstRate") && itemData.get("sgstRate") != null) {
                    item.setSgstRate(((Number) itemData.get("sgstRate")).doubleValue());
                }
                if (itemData.containsKey("igstRate") && itemData.get("igstRate") != null) {
                    item.setIgstRate(((Number) itemData.get("igstRate")).doubleValue());
                }
                
                items.add(item);
            }
            invoice.setItems(items);
        }
        
        Long clientId = ((Number) request.get("clientId")).longValue();
        Long companyId = ((Number) request.get("companyId")).longValue();
        
        return ResponseEntity.ok(invoiceService.createInvoice(invoice, clientId, companyId));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoice) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, invoice));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Invoice>> searchInvoices(@RequestParam String query) {
        return ResponseEntity.ok(invoiceService.searchInvoices(query));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Invoice>> getInvoicesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status));
    }
}
