package com.example.service;

import com.example.model.Product;
import com.example.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.UUID;
@Service

public class ProductService {

private final ProductRepository productRepository;

public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

//public Product addProduct(Product product) {
//    return productRepository.addProduct(product);
//}

    public Product addProduct(Product product) {
        try {
            return productRepository.addProduct(product);
        } catch (IllegalArgumentException e) {
            System.err.println("Error adding product: " + e.getMessage());
            throw e; // ✅ Rethrow to make sure the test catches it
        }
    }
    public ArrayList<Product> getProducts() {
        return productRepository.getProducts();
    }

    public Product getProductById(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        return productRepository.getProductById(productId);
    }

//    public Product updateProduct(UUID productId, String newName, double newPrice) {
//        return productRepository.updateProduct(productId, newName, newPrice);
//    }

    public Product updateProduct(UUID productId, String newName, double newPrice) {
        try {
            return productRepository.updateProduct(productId, newName, newPrice);
        } catch (IllegalArgumentException e) {
            System.err.println("Error updating product: " + e.getMessage());
            throw e; // ✅ Rethrow so the test can catch it
        }
    }

    //    public void applyDiscount(double discount, ArrayList<UUID> productIds) {
//        productRepository.applyDiscount(discount, productIds);
//    }
public void applyDiscount(double discount, ArrayList<UUID> productIds) {
    try {
        productRepository.applyDiscount(discount, productIds);
    } catch (IllegalArgumentException e) {
        System.err.println("Error applying discount: " + e.getMessage());
        throw e; // ✅ Rethrow so the test can catch it
    }
}


    public void deleteProductById(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        productRepository.deleteProductById(productId);
    }


}
