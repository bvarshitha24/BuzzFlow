
package com.example.backend.service;

import com.example.backend.model.Payment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class PaymentService {

    private final List<Payment> payments =
            new CopyOnWriteArrayList<>();

    public List<Payment> getAllPayments() {
        return payments;
    }

    public Payment addPayment(Payment payment) {
        payments.add(payment);
        return payment;
    }
}
