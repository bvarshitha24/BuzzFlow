
package com.example.backend.service;

import com.example.backend.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ProductService {

    private final List<Product> products =
            new CopyOnWriteArrayList<>();

    public List<Product> getAllProducts() {
        return products;
    }

    public Product addProduct(Product product) {
        products.add(product);
        return product;
    }

    public boolean deleteProduct(Long id) {
        return products.removeIf(
                product -> product.getId().equals(id)
        );
    }
}
