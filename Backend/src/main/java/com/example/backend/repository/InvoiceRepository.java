
package com.example.backend.repository;

import com.example.backend.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository
        extends JpaRepository<Invoice, Long> {

    List<Invoice> findByCustomerId(Long customerId);

    List<Invoice> findByStatusIgnoreCase(String status);
}
