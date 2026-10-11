
package com.example.backend.service;

import com.example.backend.model.Invoice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class InvoiceService {

    private final List<Invoice> invoices =
            new CopyOnWriteArrayList<>();

    public List<Invoice> getAllInvoices() {
        return invoices;
    }

    public Invoice createInvoice(Invoice invoice) {

        if (invoice.getStatus() == null
                || invoice.getStatus().isBlank()) {
            invoice.setStatus("PENDING");
        }

        invoices.add(invoice);
        return invoice;
    }

    public Invoice findInvoice(Long id) {
        return invoices.stream()
                .filter(invoice -> invoice.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
