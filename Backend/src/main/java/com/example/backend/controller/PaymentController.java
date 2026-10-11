
package com.example.backend.controller;

import com.example.backend.model.Payment;
import com.example.backend.service.PaymentDatabaseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentDatabaseService paymentService;

    public PaymentController(
            PaymentDatabaseService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<Payment> getPayments() {
        return paymentService.getAllPayments();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment createPayment(
            @RequestBody PaymentRequestBody request) {

        Payment payment = new Payment();
        payment.setAmount(request.amount());
        payment.setPaymentDate(request.paymentDate());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setReferenceNumber(request.referenceNumber());

        return paymentService.recordPayment(
                payment, request.invoiceId());
    }

    public record PaymentRequestBody(
            Long invoiceId,
            java.math.BigDecimal amount,
            java.time.LocalDate paymentDate,
            String paymentMethod,
            String referenceNumber) {
    }
}
