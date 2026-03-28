package com.invoice.invoice.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String description;
    private String hsnCode;
    private Integer quantity;
    private Double unitPrice;
    private Double amount;
    private Double taxableValue;
    private Double cgstRate;
    private Double cgstAmount;
    private Double sgstRate;
    private Double sgstAmount;
    private Double igstRate;
    private Double igstAmount;
    
    @ManyToOne
    @JoinColumn(name = "invoice_id")
    @JsonIgnore
    private Invoice invoice;
    
    public InvoiceItem() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getHsnCode() { return hsnCode; }
    public void setHsnCode(String hsnCode) { this.hsnCode = hsnCode; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public Double getTaxableValue() { return taxableValue; }
    public void setTaxableValue(Double taxableValue) { this.taxableValue = taxableValue; }
    
    public Double getCgstRate() { return cgstRate; }
    public void setCgstRate(Double cgstRate) { this.cgstRate = cgstRate; }
    
    public Double getCgstAmount() { return cgstAmount; }
    public void setCgstAmount(Double cgstAmount) { this.cgstAmount = cgstAmount; }
    
    public Double getSgstRate() { return sgstRate; }
    public void setSgstRate(Double sgstRate) { this.sgstRate = sgstRate; }
    
    public Double getSgstAmount() { return sgstAmount; }
    public void setSgstAmount(Double sgstAmount) { this.sgstAmount = sgstAmount; }
    
    public Double getIgstRate() { return igstRate; }
    public void setIgstRate(Double igstRate) { this.igstRate = igstRate; }
    
    public Double getIgstAmount() { return igstAmount; }
    public void setIgstAmount(Double igstAmount) { this.igstAmount = igstAmount; }
    
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
}
