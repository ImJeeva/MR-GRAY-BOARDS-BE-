package com.invoice.invoice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "invoice_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
