package com.example.MiniProject1;

import com.example.model.Product;
import com.example.repository.ProductRepository;
import com.example.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;
    private Product testProduct;

    private Product product;
    private UUID testProductId; // ✅ Declare testProductId



@BeforeEach
void setUp() {
    // ✅ Ensure repository is fully cleared before each test
    productRepository.saveAll(new ArrayList<>()); // ✅ Completely clears stored data

    // ✅ Add a single product and retrieve its ID
    testProduct = new Product(UUID.randomUUID(), "Laptop", 1500.00);
    productService.addProduct(testProduct);

    // ✅ Assign valid product ID for reference in tests
    testProductId = testProduct.getId();

    // ✅ Assign `product` to reference the same test product
    product = testProduct;
}


    // 1️⃣ Test: Add Product
    @Test
    void testAddProduct() {
        Product newProduct = new Product(UUID.randomUUID(), "Phone", 800.00);
        Product savedProduct = productService.addProduct(newProduct);

        assertNotNull(savedProduct);
        assertEquals("Phone", savedProduct.getName());
        assertEquals(800.00, savedProduct.getPrice());
    }

    @Test
    void testAddProductWithNullName() {
        // Step 1: Create a product with a null name
        Product product = new Product(null, null, 50.00);

        // Step 2: Expect an IllegalArgumentException when calling addProduct
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(product);
        });

        // Step 3: Verify the exception message
        assertEquals("Product name cannot be null", exception.getMessage());
    }
    @Test
    void testAddProductWithZeroPrice() {
        // Step 1: Create a product with a price of 0.00
        Product product = new Product(null, "Free Item", 0.00);

        // Step 2: Add product to the service
        Product savedProduct = productService.addProduct(product);

        // Step 3: Verify that the product was added and price remains 0.00
        assertNotNull(savedProduct, "Product should not be null");
        assertEquals(0.00, savedProduct.getPrice(), "Product price should be zero");
    }


    @Test
    void testGetProductsWhenListIsEmpty() {
        // Step 1: Ensure the repository is empty
        ArrayList<Product> existingProducts = productService.getProducts();
        for (Product product : existingProducts) {
            productService.deleteProductById(product.getId());
        }

        // Step 2: Call getProducts
        ArrayList<Product> products = productService.getProducts();

        // Step 3: Verify that the list is empty but not null
        assertNotNull(products, "Product list should not be null");
        assertTrue(products.isEmpty(), "Product list should be empty when no products exist");
    }

    @Test
    void testGetProductsAfterUpdatingProduct() {
        // ✅ Step 1: Get initial count of products
        int initialSize = productService.getProducts().size();

        // ✅ Step 2: Add a product
        Product product = new Product(UUID.randomUUID(), "Laptop", 1500.00);
        productService.addProduct(product);

        // ✅ Step 3: Update the product
        String newName = "Updated Laptop";
        double newPrice = 1200.00;
        productService.updateProduct(product.getId(), newName, newPrice);

        // ✅ Step 4: Fetch all products
        ArrayList<Product> products = productService.getProducts();

        // ✅ Step 5: Assertions
        assertNotNull(products, "The product list should not be null.");
        assertEquals(initialSize + 1, products.size(), "The product list should contain exactly one new product.");

        // ✅ Step 6: Find the updated product
        Product updatedProduct = products.stream()
                .filter(p -> p.getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(updatedProduct, "Updated product should be found.");
        assertEquals(newName, updatedProduct.getName(), "The product name should be updated.");
        assertEquals(newPrice, updatedProduct.getPrice(), "The product price should be updated.");
    }


    @Test
    void testGetProducts_AfterDeletion() {
        // ✅ Delete the test product
        productService.deleteProductById(testProductId);

        // ✅ Fetch products
        ArrayList<Product> products = productService.getProducts();

        // ✅ Assertions
        assertNotNull(products, "Product list should not be null");
        assertFalse(products.contains(testProduct), "Deleted product should not exist in the list");
    }


    @Test
    void testGetProductById_NullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.getProductById(null);
        });

        assertEquals("Product ID cannot be null", exception.getMessage());
    }


    @Test
    void testGetProductByIdWithInvalidId() {
        // Generate a random UUID that does not exist in the repository
        UUID invalidId = UUID.randomUUID();

        // Call the service method
        Product foundProduct = productService.getProductById(invalidId);

        // Assertions
        assertNull(foundProduct, "The returned product should be null for an invalid ID.");
    }

    @Test
    void testGetProductByIdAfterApplyingDiscount() {
        // Add a product to the repository
        Product product = new Product(UUID.randomUUID(), "Laptop", 1500.00);
        productService.addProduct(product);

        // Apply a discount of 20%
        double discount = 20.0;
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(product.getId());
        productService.applyDiscount(discount, productIds);

        // Call the service method
        Product foundProduct = productService.getProductById(product.getId());

        // Assertions
        assertNotNull(foundProduct, "The product should not be null.");
        assertEquals(1200.00, foundProduct.getPrice(), "The product price should be discounted by 20%.");
    }
    // 3️⃣ Test: Get Product By ID
    @Test
    void testGetProductById() {
        Product foundProduct = productService.getProductById(product.getId());

        assertNotNull(foundProduct);
        assertEquals(product.getId(), foundProduct.getId());
        assertEquals("Laptop", foundProduct.getName());
    }

    // 4️⃣ Test: Update Product

    @Test
    void testUpdateNonExistentProduct() {
        // Step 1️⃣: Generate a random UUID that does not exist
        UUID nonExistentId = UUID.randomUUID();

        // Step 2️⃣: Attempt to update the product
        Product updatedProduct = productService.updateProduct(nonExistentId, "Nonexistent Product", 900.00);

        // Step 3️⃣: Ensure the product update was unsuccessful
        assertNull(updatedProduct, "Updating a non-existent product should return null");
    }
    @Test
    void testUpdateProductWithInvalidData() {
        // Step 1️⃣: Ensure the product exists
        assertNotNull(productService.getProductById(testProductId), "Product should exist before updating");

        // Step 2️⃣: Attempt to update with invalid data
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProduct(testProductId, null, -500.00);
        });

        // Step 3️⃣: Ensure the correct exception message is thrown
        assertEquals("Invalid product update parameters", exception.getMessage());
    }
    @Test
    void testUpdateProductWithSameNameAndPrice() {
        // Step 1️⃣: Ensure the product exists
        assertNotNull(productService.getProductById(testProductId), "Product should exist before updating");

        // Step 2️⃣: Attempt to update the product with the same name and price
        Product updatedProduct = productService.updateProduct(testProductId, testProduct.getName(), testProduct.getPrice());

        // Step 3️⃣: Validate that the product remains unchanged
        assertNotNull(updatedProduct, "Updated product should not be null");
        assertEquals(testProduct.getId(), updatedProduct.getId(), "Product ID should remain unchanged");
        assertEquals(testProduct.getName(), updatedProduct.getName(), "Product name should remain unchanged");
        assertEquals(testProduct.getPrice(), updatedProduct.getPrice(), "Product price should remain unchanged");
    }

@Test
void testUpdateProductWithNegativePrice() {
    // Ensure the product exists
    assertNotNull(productService.getProductById(testProductId), "Product should exist before update");

    // Attempt to update with a negative price
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        productService.updateProduct(testProductId, "Updated Laptop", -100.00);
    });

    // Verify the exception message
    String expectedMessage = "Invalid product update parameters"; // Ensure this is EXACTLY what your method throws
    assertEquals(expectedMessage, exception.getMessage(), "Exception message should match expected output");
}


    @Test
    void testApplyNegativeDiscount() {
        // Step 1️⃣: Ensure the product exists before applying discount
        assertNotNull(productService.getProductById(testProductId), "Product should exist before applying discount");

        // Step 2️⃣: Attempt to apply a negative discount
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ArrayList<UUID> productIds = new ArrayList<>();
            productIds.add(testProductId);
            productService.applyDiscount(-10.0, productIds);
        });

        // Step 3️⃣: Ensure the correct exception message is thrown
        assertEquals("Discount cannot be negative", exception.getMessage());
    }

    @Test
    void testApplyDiscountToNonExistingProduct() {
        // Step 1: Create a random UUID that does not exist in the system
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(UUID.randomUUID());  // Non-existing product ID

        // Step 2: Ensure the method does not throw an error
        assertDoesNotThrow(() -> productService.applyDiscount(10.0, productIds));
    }
    @Test
    void testApplyZeroPercentDiscount() {
        // Step 1: Add a product
        Product product = new Product(UUID.randomUUID(), "Monitor", 500.00);
        productService.addProduct(product);

        // Step 2: Apply 0% discount
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(product.getId());
        productService.applyDiscount(0.0, productIds);

        // Step 3: Retrieve the product and check if price remains the same
        Product unchangedProduct = productService.getProductById(product.getId());

        assertNotNull(unchangedProduct);
        assertEquals(500.00, unchangedProduct.getPrice());  // Price should remain unchanged
    }



    @Test
    void testDeleteNonExistingProduct() {
        // Step 1: Use a random UUID (not in database)
        UUID nonExistingId = UUID.randomUUID();

        // Step 2: Ensure the method does not throw an error
        assertDoesNotThrow(() -> productService.deleteProductById(nonExistingId));
    }
    @Test
    void testDeleteProductWithNullId() {
        // Step 1️⃣: Ensure null ID throws an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.deleteProductById(null);
        });

        // Step 2️⃣: Verify the exception message
        assertEquals("Product ID cannot be null", exception.getMessage());
    }


    @Test
    void testDeleteExistingProduct() {
        // Ensure the product exists
        assertNotNull(productService.getProductById(testProductId), "Product should exist before deletion");

        // Delete the product
        productService.deleteProductById(testProductId);

        // Verify the product is deleted
        Product deletedProduct = productService.getProductById(testProductId);
        assertNull(deletedProduct, "Product should be null after deletion");
    }
    @Test
    void testDeleteProductTwice() {
        // Delete the product once
        productService.deleteProductById(testProductId);

        // Try deleting it again
        assertDoesNotThrow(() -> productService.deleteProductById(testProductId), "Deleting the same product twice should not throw an error");

        // Ensure product is still not found
        assertNull(productService.getProductById(testProductId), "Product should remain deleted");
    }


}
