package com.example.repository;

import com.example.model.Product;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ProductRepository extends MainRepository<Product> {


    public ProductRepository() {}

    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/products.json";
    }
    @Override
    protected Class<Product[]> getArrayType() {
        return Product[].class;
    }
    public Product addProduct(Product product) {
        if(product.getId() == null){
            product.setId(UUID.randomUUID());
        }
        ArrayList<Product> productList = findAll();  // ✅ Load from JSON file
        productList.add(product);
        saveAll(productList);  // ✅ Save back to JSON
        return product;
    }
    public ArrayList<Product> getProducts() {
        return findAll();
    }
    public Product getProductById(UUID productId) {
        return findAll().stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElse(null);
    }


    public Product updateProduct(UUID productId, String newName, double newPrice) {
        ArrayList<Product> productList = findAll();  // ✅ Load from JSON file
        for (Product product : productList) {
            if (product.getId().equals(productId)) {
                product.setName(newName);
                product.setPrice(newPrice);
                saveAll(productList);  // ✅ Save back to JSON
                return product;
            }
        }
        return null;
    }
    public void applyDiscount(double discount, ArrayList<UUID> productIds) {
        ArrayList<Product> productList = findAll();
        boolean updated = false;

        for (Product product : productList) {
            if (productIds.contains(product.getId())) {
                double newPrice = product.getPrice() * (1 - discount / 100);
                product.setPrice(newPrice);
                updated = true;
            }
        }

        if (updated) {
            saveAll(productList);
        }
    }

    public void deleteProductById(UUID productId) {
    ArrayList<Product> productList = findAll();
    productList.removeIf(product -> product.getId().equals(productId));
    saveAll(productList);
}

}
