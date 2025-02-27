package com.example.controller;

import com.example.model.Product;
import com.example.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @GetMapping("/")
    public ArrayList<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable UUID productId) {
        return productService.getProductById(productId);
    }

    @PutMapping("/{productId}")
    public Product updateProduct(@PathVariable UUID productId, @RequestBody Map<String, Object> updates) {
        String newName = (String) updates.get("newName");
        double newPrice = ((Number) updates.get("newPrice")).doubleValue();
        return productService.updateProduct(productId, newName, newPrice);
    }


    @PutMapping("/applyDiscount")
    public void applyDiscount(@RequestBody Map<String, Object> discountData) {
        double discount = ((Number) discountData.get("discount")).doubleValue();
        ArrayList<UUID> productIds = new ArrayList<>();
        for (String id : (ArrayList<String>) discountData.get("productIds")) {
            productIds.add(UUID.fromString(id));
        }
        productService.applyDiscount(discount, productIds);
    }


    @DeleteMapping("/delete/{productId}")
    public String deleteProductById(@PathVariable UUID productId) {
        productService.deleteProductById(productId);
        return "Product deleted successfully!";
    }
}