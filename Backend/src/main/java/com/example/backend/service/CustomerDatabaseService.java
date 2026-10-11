
package com.example.backend.service;

import com.example.backend.model.Customer;
import com.example.backend.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerDatabaseService {

    private final CustomerRepository customerRepository;

    public CustomerDatabaseService(
            CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer not found"));
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException(
                    "A customer with this email already exists");
        }

        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Customer not found");
        }

        customerRepository.deleteById(id);
    }
}
