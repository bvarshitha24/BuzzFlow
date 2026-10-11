
package com.example.backend.service;

import com.example.backend.model.Customer;
import com.example.backend.model.Invoice;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InvoiceDatabaseService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    public InvoiceDatabaseService(
            InvoiceRepository invoiceRepository,
            CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoice(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invoice not found"));
    }

    @Transactional
    public Invoice createInvoice(Invoice invoice, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer not found"));

        invoice.setCustomer(customer);

        if (invoice.getPaidAmount() == null) {
            invoice.setPaidAmount(BigDecimal.ZERO);
        }

        if (invoice.getStatus() == null
                || invoice.getStatus().isBlank()) {
            invoice.setStatus("PENDING");
        }

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice saveInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }
}
