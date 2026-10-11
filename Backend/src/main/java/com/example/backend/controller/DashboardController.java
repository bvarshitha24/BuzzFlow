
package com.example.backend.controller;

import com.example.backend.model.Invoice;
import com.example.backend.service.InvoiceDatabaseService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final InvoiceDatabaseService invoiceService;

    public DashboardController(
            InvoiceDatabaseService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        List<Invoice> invoices =
                invoiceService.getAllInvoices();

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal paid = BigDecimal.ZERO;
        BigDecimal outstanding = BigDecimal.ZERO;
        int pending = 0;

        for (Invoice invoice : invoices) {
            if (invoice.getTotalAmount() != null) {
                total = total.add(invoice.getTotalAmount());
            }

            if (invoice.getPaidAmount() != null) {
                paid = paid.add(invoice.getPaidAmount());
            }

            outstanding = outstanding.add(
                    invoice.getOutstandingAmount());

            if ("PENDING".equalsIgnoreCase(
                    invoice.getStatus())) {
                pending++;
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalInvoices", invoices.size());
        summary.put("totalAmount", total);
        summary.put("paidAmount", paid);
        summary.put("outstandingAmount", outstanding);
        summary.put("pendingInvoices", pending);

        return summary;
    }
}
