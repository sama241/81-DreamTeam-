package com.example.MiniProject1;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.UserRepository;
import com.example.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${spring.application.cartDataPath}")
    private String cartDataPath;

    @Value("${spring.application.userDataPath}")
    private String userDataPath;

    private UUID userId;
    private UUID userId2;
    private UUID cartId;
    private UUID cartId2;
    private Product testProduct;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        cartId = UUID.randomUUID();
        cartId2 = UUID.randomUUID();
        testProduct = new Product(UUID.randomUUID(), "Test Product", 10.0);

        //reset json
        try {
            objectMapper.writeValue(new File(cartDataPath), new ArrayList<Cart>());
            objectMapper.writeValue(new File(userDataPath), new ArrayList<User>());
        } catch (IOException e) {
            throw new RuntimeException("Failed to clear JSON data", e);
        }

        User testUser = new User();
        testUser.setId(userId);
        testUser.setName("Test User");
        userRepository.addUser(testUser);

        User testUser2 = new User();
        testUser2.setId(userId2);
        testUser2.setName("Test User2");
        userRepository.addUser(testUser2);
    }

    //  1. Test Adding a Cart (3 tests)
    @Test
    void testAddCart_ShouldCreateNewCart() {
        Cart cart = new Cart(cartId, userId, new ArrayList<>());
        Cart createdCart = cartService.addCart(cart);

        assertNotNull(createdCart, "Cart should be created");
        assertEquals(userId, createdCart.getUserId(), "User ID should match");
    }

    @Test
    void testAddCart_WhenCartAlreadyExists_ShouldReturnExisting() {
        // Create and save a user
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setName("Test User");
        userRepository.addUser(user);

        // Create and save a cart
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId, userId, new ArrayList<>());
        cartService.addCart(cart);

        // Try to add the same cart again
        Cart duplicateCart = cartService.addCart(cart);

        // Verify that the existing cart is returned
        assertEquals(cartId, duplicateCart.getId(), "Should return existing cart");
    }

    @Test
    void testAddCart_WithNullUserId_ShouldReturnNull() {
        Cart cart = new Cart(cartId, null, new ArrayList<>());
        Cart result = cartService.addCart(cart);
        assertNull(result, "Should return null for cart with null userId");
    }

    //  2. Test Retrieving a Cart by ID (3 tests)
    @Test
    void testGetCartById_WhenCartExists_ShouldReturnCart() {
        Cart cart = new Cart(cartId, userId, new ArrayList<>());
        cartService.addCart(cart);

        Cart foundCart = cartService.getCartById(cartId);

        assertNotNull(foundCart, "Cart should be found");
        assertEquals(cartId, foundCart.getId(), "Cart ID should match");
    }

    @Test
    void testGetCartById_WhenCartDoesNotExist_ShouldReturnNull() {
        Cart foundCart = cartService.getCartById(UUID.randomUUID());
        assertNull(foundCart, "Should return null if cart does not exist");
    }

    @Test
    void testGetCartById_WithNullId_ShouldReturnNull() {
        Cart result = cartService.getCartById(null);
        assertNull(result, "Should return null for null cartId");
    }

    //  3. Test Retrieving All Carts (3 tests)
    @Test
    void testGetCarts_WhenNoCarts_ShouldReturnEmptyList() {
        assertTrue(cartService.getCarts().isEmpty(), "Should return an empty list if no carts exist");
    }

    @Test
    void testGetCarts_WhenMultipleCartsExist_ShouldReturnAll() {
        // Step 1: Create users and add them to the repository
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        User user1 = new User(userId1, "User 1", new ArrayList<>());
        User user2 = new User(userId2, "User 2", new ArrayList<>());

        userRepository.addUser(user1);
        userRepository.addUser(user2);

        // Step 2: Create carts with existing users
        Cart cart1 = new Cart(UUID.randomUUID(), userId1, new ArrayList<>());
        Cart cart2 = new Cart(UUID.randomUUID(), userId2, new ArrayList<>());

        cartService.addCart(cart1);
        cartService.addCart(cart2);

        // Step 3: Verify the carts were added
        assertEquals(2, cartService.getCarts().size(), "Should return all existing carts");
    }


    @Test
    void testGetCarts_WhenOneCartExists_ShouldReturnListOfOne() {
        Cart cart = new Cart(cartId, userId, new ArrayList<>());
        cartService.addCart(cart);

        assertEquals(1, cartService.getCarts().size(), "Should return a list with one cart");
    }

    //  4. Test Getting Cart by User ID (3 tests)
    @Test
    void testGetCartByUserId_WhenCartExists_ShouldReturnCart() {
        Cart cart = new Cart(cartId, userId, new ArrayList<>());
        cartService.addCart(cart);

        assertNotNull(cartService.getCartByUserId(userId), "Cart should be found for user");
    }

    @Test
    void testGetCartByUserId_WhenNoCartExists_ShouldReturnNull() {
        assertNull(cartService.getCartByUserId(userId), "Should return null for a user with no cart");
    }

    @Test
    void testGetCartByUserId_WithNullUserId_ShouldReturnNull() {
        Cart result = cartService.getCartByUserId(null);
        assertNull(result, "Should return null for null userId");
    }

    //  5. Test Adding a Product to a Cart (3 tests)
    @Test
    void testAddProductToCart_ShouldAddProductSuccessfully() {
        // Create and add a new cart
        Cart cart = new Cart(cartId, userId, new ArrayList<>());
        cartService.addCart(cart);

        //  Ensure the cart exists before adding the product
        Cart retrievedCart = cartService.getCartById(cartId);
        assertNotNull(retrievedCart, "Cart should exist before adding a product");
        assertTrue(retrievedCart.getProducts().isEmpty(), "Cart should be empty initially");

        //  Add a product to the cart
        cartService.addProductToCart(cartId, testProduct);

        //  Fetch the updated cart to verify persistence
        Cart updatedCart = cartService.getCartById(cartId);
        System.out.println("Updated Cart: " + updatedCart.getProducts());

        //  Ensure the cart is not empty
        assertFalse(updatedCart.getProducts().isEmpty(), "Cart should not be empty after adding a product");

        //  Check if the product is present in the cart (by ID)
        assertTrue(
                updatedCart.getProducts().stream()
                        .anyMatch(p -> p.getId().equals(testProduct.getId())),
                "Product should be added to the cart"
        );

        //  Optional: Direct comparison of first product (if only one product exists)
        assertEquals(
                testProduct.getId(),
                updatedCart.getProducts().get(0).getId(),
                "Product ID should match"
        );
    }




    @Test
    void testAddProductToCart_WhenCartDoesNotExist_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addProductToCart(UUID.randomUUID(), testProduct), "Should throw exception for non-existing cart");
    }


    @Test
    void testAddProductToCart_WithNullProduct_ShouldThrowException() {
        Cart cart = new Cart(cartId, userId, new ArrayList<>());
        cartService.addCart(cart);

        assertThrows(IllegalArgumentException.class, () -> cartService.addProductToCart(cartId, null), "Should throw exception for null product");
    }

    // ✅ 6. Test Removing a Product from a Cart (3 tests)
    @Test
    void testDeleteProductFromCart_ShouldRemoveProduct() {
        Cart cart = new Cart(cartId, userId, new ArrayList<>());
        cart.getProducts().add(testProduct);
        cartService.addCart(cart);

        cartService.deleteProductFromCart(cartId, testProduct);
        Cart updatedCart = cartService.getCartById(cartId);

        assertFalse(updatedCart.getProducts().contains(testProduct), "Product should be removed from cart");
    }

    @Test
    void testDeleteProductFromCart_WhenCartDoesNotExist_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> cartService.deleteProductFromCart(cartId, testProduct), "Should throw exception for non-existing cart");
    }

    @Test
    void testDeleteProductFromCart_WithNullProduct_ShouldThrowException() {
        Cart cart = new Cart(cartId, userId, new ArrayList<>());
        cartService.addCart(cart);

        assertThrows(IllegalArgumentException.class, () -> cartService.deleteProductFromCart(cartId, null), "Should throw exception for null product");
    }

    // ✅ 7. Test Deleting a Cart (3 tests)
    @Test
    void testDeleteCartById_ShouldRemoveCart() {
        Cart cart = new Cart(cartId, userId, new ArrayList<>());
        cartService.addCart(cart);

        cartService.deleteCartById(cartId);

        assertNull(cartService.getCartById(cartId), "Cart should be deleted");
    }

    @Test
    void testDeleteCartById_WhenCartDoesNotExist_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> cartService.deleteCartById(UUID.randomUUID()), "Should throw exception for non-existing cart");
    }

    @Test
    void testDeleteCartById_WithNullId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> cartService.deleteCartById(null), "Should throw exception for null cartId");
    }
}
