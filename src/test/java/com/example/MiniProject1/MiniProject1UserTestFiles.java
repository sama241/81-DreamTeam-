//package com.example.MiniProject1;
//
//import com.example.model.Cart;
//import com.example.model.Order;
//import com.example.model.Product;
//import com.example.model.User;
//import com.example.repository.CartRepository;
//import com.example.repository.OrderRepository;
//import com.example.repository.UserRepository;
//import com.example.service.CartService;
//import com.example.service.OrderService;
//import com.example.service.UserService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class MiniProject1UserTestFiles {
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private CartService cartService;
//
//    @Autowired
//    private CartRepository cartRepository;
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    private User testUser;
//    private UUID testUserId;
//    private UUID testOrderId;
//    private Order testOrder;
//    private Cart testCart;
//    private List<Product> testProducts;
//    @BeforeEach
//    void setUp() {
//        testUserId = UUID.randomUUID();
//        testUser = new User(testUserId, "Test User", new ArrayList<>());
//        userService.addUser(testUser);
//
//        testOrderId = UUID.randomUUID();
//        testOrder = new Order(testOrderId, testUserId, 100.0, new ArrayList<>());
//        userRepository.addOrderToUser(testUserId, testOrder);
//
//        List<Order> orders = userRepository.getOrdersByUserId(testUserId);
//        assertTrue(orders.stream().anyMatch(o -> o.getId().equals(testOrderId)), "Order should be added to the user");
//
//        testProducts = List.of(new Product(UUID.randomUUID(), "Test Product", 50.0));
//        testCart = new Cart(UUID.randomUUID(), testUserId, testProducts);
//        cartRepository.save(testCart); // ✅ Save the cart in real storage
//    }
//
//
//    @AfterEach
//    void tearDown() {
//        System.out.println("Before deletion: " + userRepository.findAll()); // Debugging
//        userRepository.deleteUserById(testUserId);
//        System.out.println("After deletion: " + userRepository.findAll()); // Debugging
//        Cart cart = cartService.getCartByUserId(testUserId);
//        if (cart != null) {
//            cartRepository.deleteCartById(cart.getId()); // Delete cart using its ID
//        }
//
//        assertNull(userRepository.getUserById(testUserId), "User should be deleted after the test");
//
//    }
//
//    @Test
//    void testAddUserSuccessfully() {
//        User newUser = new User(UUID.randomUUID(), "New User", new ArrayList<>());
//        User createdUser = userService.addUser(newUser);
//
//        assertNotNull(createdUser);
//        assertEquals(newUser.getName(), createdUser.getName());
//    }
//
//    @Test
//    void testAddUserDuplicateId() {
//        User duplicateUser = new User(testUser.getId(), "Another User", new ArrayList<>()); // Same ID as testUser
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userService.addUser(duplicateUser);
//        });
//
//        assertEquals("User with this ID already exists", exception.getMessage());
//    }
//
//    @Test
//    void testAddUserMissingName() {
//        User invalidUser = new User(UUID.randomUUID(), null, new ArrayList<>());
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userService.addUser(invalidUser);
//        });
//
//        assertEquals("User name cannot be empty", exception.getMessage());
//    }
//
//    @Test
//    void testGetUserById_Success() {
//        User foundUser = userService.getUserById(testUserId);
//
//        assertNotNull(foundUser, "User should not be null");
//        assertEquals(testUser.getId(), foundUser.getId(), "User ID should match");
//        assertEquals(testUser.getName(), foundUser.getName(), "User name should match");
//    }
//
//    @Test
//    void testGetUserById_UserNotFound() {
//        UUID nonExistentUserId = UUID.randomUUID();
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userService.getUserById(nonExistentUserId);
//        });
//
//        assertEquals("User not found", exception.getMessage());
//    }
//
//    @Test
//    void testGetUserById_NullId() {
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            userService.getUserById(null);
//        });
//
//        assertEquals("User ID cannot be null", exception.getMessage());
//    }
//    @Test
//    void testRemoveOrderFromUser_Success() {
//        userService.removeOrderFromUser(testUserId, testOrderId);
//
//        List<Order> orders = userRepository.getOrdersByUserId(testUserId);
//        assertFalse(orders.contains(testOrder), "Order should be removed from the user");
//    }
//
//    @Test
//    void testRemoveOrderFromUser_NonExistentOrder() {
//        UUID nonExistentOrderId = UUID.randomUUID();
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userService.removeOrderFromUser(testUserId, nonExistentOrderId);
//        });
//
//        assertEquals("Order not found for user", exception.getMessage());
//    }
//
//    @Test
//    void testRemoveOrderFromNonExistentUser() {
//        UUID nonExistentUserId = UUID.randomUUID();
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userService.removeOrderFromUser(nonExistentUserId, testOrderId);
//        });
//
//        assertEquals("User not found", exception.getMessage());
//    }
//
//    @Test
//    void testAddOrderToUser_Success() {
//        userService.addOrderToUser(testUserId);
//
//        List<Order> orders = userRepository.getOrdersByUserId(testUserId);
//        assertNotNull(orders, "Orders list should not be null");
//        assertFalse(orders.isEmpty(), "User should have orders");
//        assertEquals(2, orders.size(), "User should have exactly two orders"); // One from setup, one new
//        assertEquals(testUserId, orders.get(1).getUserId(), "New order should belong to the correct user");
//
//        assertNull(cartService.getCartByUserId(testUserId), "Cart should be deleted after checkout");
//    }
//    @Test
//    void testAddOrderToUser_NoCart() {
//        Cart cart = cartService.getCartByUserId(testUserId);
//        if (cart != null) {
//            cartRepository.deleteCartById(cart.getId());
//        }
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userService.addOrderToUser(testUserId);
//        });
//
//        assertEquals("Cart not found for user", exception.getMessage());
//    }
//    @Test
//    void testAddOrderToUser_EmptyCart() {
//
//        Cart existingCart = cartService.getCartByUserId(testUserId);
//        if (existingCart != null) {
//            cartRepository.deleteCartById(existingCart.getId());
//        }
//
//        Cart emptyCart = new Cart(UUID.randomUUID(), testUserId, new ArrayList<>());
//        cartRepository.save(emptyCart);
//
//        Cart retrievedCart = cartService.getCartByUserId(testUserId);
//        assertNotNull(retrievedCart, "Cart should be saved before calling addOrderToUser()");
//        assertEquals(0, retrievedCart.getProducts().size(), "Cart should have zero products");
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userService.addOrderToUser(testUserId);
//        });
//
//        assertEquals("Cannot place order with an empty cart", exception.getMessage());
//    }
//}