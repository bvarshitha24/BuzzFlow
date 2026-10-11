
package com.example.backend.controller;

import com.example.backend.model.Invoice;
import com.example.backend.service.InvoiceDatabaseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceDatabaseService invoiceService;

    public InvoiceController(
            InvoiceDatabaseService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public List<Invoice> getInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    public Invoice getInvoice(@PathVariable Long id) {
        return invoiceService.getInvoice(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Invoice createInvoice(
            @RequestBody InvoiceRequestBody request) {

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(request.invoiceNumber());
        invoice.setInvoiceDate(request.invoiceDate());
        invoice.setDueDate(request.dueDate());
        invoice.setTotalAmount(request.totalAmount());

        return invoiceService.createInvoice(
                invoice, request.customerId());
    }

    public record InvoiceRequestBody(
            String invoiceNumber,
            Long customerId,
            java.time.LocalDate invoiceDate,
            java.time.LocalDate dueDate,
            java.math.BigDecimal totalAmount) {
    }
}
