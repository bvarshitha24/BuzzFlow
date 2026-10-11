
package com.example.backend.service;

import com.example.backend.model.Invoice;
import com.example.backend.model.Payment;
import com.example.backend.repository.InvoiceRepository;
import com.example.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentDatabaseService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public PaymentDatabaseService(
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Transactional
    public Payment recordPayment(Payment payment, Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invoice not found"));

        if (payment.getAmount() == null
                || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be positive");
        }

        BigDecimal outstanding = invoice.getOutstandingAmount();

        if (payment.getAmount().compareTo(outstanding) > 0) {
            throw new IllegalArgumentException(
                    "Payment exceeds the outstanding amount");
        }

        payment.setInvoice(invoice);

        BigDecimal paid = invoice.getPaidAmount() == null
                ? BigDecimal.ZERO : invoice.getPaidAmount();

        invoice.setPaidAmount(paid.add(payment.getAmount()));

        if (invoice.getOutstandingAmount()
                .compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus("PAID");
        } else {
            invoice.setStatus("PARTIALLY_PAID");
        }

        invoiceRepository.save(invoice);

        return paymentRepository.save(payment);
    }
}
