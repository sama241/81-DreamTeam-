package com.example.MiniProject1;

import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import com.example.service.CartService;
import com.example.service.OrderService;
import com.example.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MiniProject1UserTestFiles {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    private User testUser;
    private UUID testUserId;
    private UUID testOrderId;
    private Order testOrder;
    private Cart testCart;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User(testUserId, "Test User", new ArrayList<>());
        userService.addUser(testUser);

        testOrderId = UUID.randomUUID();
        testOrder = new Order(testOrderId, testUserId, 100.0, new ArrayList<>());
        userRepository.addOrderToUser(testUserId, testOrder);

        List<Order> orders = userRepository.getOrdersByUserId(testUserId);
        assertTrue(orders.stream().anyMatch(o -> o.getId().equals(testOrderId)), "Order should be added to the user");

        testProducts = List.of(new Product(UUID.randomUUID(), "Test Product", 50.0));
        testCart = new Cart(UUID.randomUUID(), testUserId, testProducts);
        cartRepository.save(testCart); // ✅ Save the cart in real storage
    }


    @AfterEach
    void tearDown() {
        System.out.println("Before deletion: " + userRepository.findAll()); // Debugging
        userRepository.clearUsers();
        System.out.println("After deletion: " + userRepository.findAll()); // Debugging
        Cart cart = cartService.getCartByUserId(testUserId);
        if (cart != null) {
            cartRepository.deleteCartById(cart.getId()); // Delete cart using its ID
        }

        assertNull(userRepository.getUserById(testUserId), "User should be deleted after the test");

    }

    @Test
    void testAddUserSuccessfully() {
        User newUser = new User(UUID.randomUUID(), "New User", new ArrayList<>());
        User createdUser = userService.addUser(newUser);

        assertNotNull(createdUser);
        assertEquals(newUser.getName(), createdUser.getName());
    }

    @Test
    void testAddUserDuplicateId() {
        User duplicateUser = new User(testUser.getId(), "Another User", new ArrayList<>()); // Same ID as testUser

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.addUser(duplicateUser);
        });

        assertEquals("User with this ID already exists", exception.getMessage());
    }

    @Test
    void testAddUserMissingName() {
        User invalidUser = new User(UUID.randomUUID(), null, new ArrayList<>());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.addUser(invalidUser);
        });

        assertEquals("User name cannot be empty", exception.getMessage());
    }

    @Test
    void testGetUserById_Success() {
        User foundUser = userService.getUserById(testUserId);

        assertNotNull(foundUser, "User should not be null");
        assertEquals(testUser.getId(), foundUser.getId(), "User ID should match");
        assertEquals(testUser.getName(), foundUser.getName(), "User name should match");
    }

    @Test
    void testGetUserById_UserNotFound() {
        UUID nonExistentUserId = UUID.randomUUID();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(nonExistentUserId);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetUserById_NullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(null);
        });

        assertEquals("User ID cannot be null", exception.getMessage());
    }

    @Test
    void testRemoveOrderFromUser_Success() {
        userService.removeOrderFromUser(testUserId, testOrderId);

        List<Order> orders = userRepository.getOrdersByUserId(testUserId);
        assertFalse(orders.contains(testOrder), "Order should be removed from the user");
    }

    @Test
    void testRemoveOrderFromUser_NonExistentOrder() {
        UUID nonExistentOrderId = UUID.randomUUID();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.removeOrderFromUser(testUserId, nonExistentOrderId);
        });

        assertEquals("Order not found for user", exception.getMessage());
    }

    @Test
    void testRemoveOrderFromNonExistentUser() {
        UUID nonExistentUserId = UUID.randomUUID();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.removeOrderFromUser(nonExistentUserId, testOrderId);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testAddOrderToUser_Success() {
        userService.addOrderToUser(testUserId);

        List<Order> orders = userRepository.getOrdersByUserId(testUserId);
        assertNotNull(orders, "Orders list should not be null");
        assertFalse(orders.isEmpty(), "User should have orders");
        assertEquals(2, orders.size(), "User should have exactly two orders"); // One from setup, one new
        assertEquals(testUserId, orders.get(1).getUserId(), "New order should belong to the correct user");

        assertNull(cartService.getCartByUserId(testUserId), "Cart should be deleted after checkout");
    }

    @Test
    void testAddOrderToUser_NoCart() {
        Cart cart = cartService.getCartByUserId(testUserId);
        if (cart != null) {
            cartRepository.deleteCartById(cart.getId());
        }

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.addOrderToUser(testUserId);
        });

        assertEquals("Cart not found for user", exception.getMessage());
    }


    /// ////Fafffff
    /// //7.2.2.2 Get the Users


    @Test
    void testGetAllUsers() {
        // Clear the repository to ensure no leftover users
        userRepository.clearUsers();
        // Arrange: Add multiple users
        User user1 = new User(UUID.randomUUID(), "faf", new ArrayList<>());
        User user2 = new User(UUID.randomUUID(), "sam", new ArrayList<>());
        userService.addUser(user1);
        userService.addUser(user2);
        // Act: Retrieve all users
        List<User> users = userService.getUsers();
        // Assert: Check the returned users list
        assertNotNull(users, "User list should not be null");
        assertEquals(2, users.size(), "Should return 2 users");

        List<String> userNames = users.stream().map(User::getName).toList();
        assertTrue(userNames.contains("faf"), "User list should contain faf");
        assertTrue(userNames.contains("sam"), "User list should contain sam");
    }

    @Test
    void testGetAllUsers_EmptyRepository() {
        // Clear the repository to ensure it's empty
        userRepository.clearUsers();

        // Act: Retrieve all users
        List<User> users = userService.getUsers();

        // Assert: Check that the list is empty
        assertNotNull(users, "User list should not be null");
        assertTrue(users.isEmpty(), "User list should be empty");
    }

    @Test
    void testGetAllUsers_LargeNumberOfUsers() {
        userRepository.clearUsers();
        assertTrue(userService.getUsers().isEmpty(), "Repository should be empty before adding users");
        // Arrange: Add 100 users
        for (int i = 0; i < 100; i++) {
            User user = new User(UUID.randomUUID(), "User " + i, new ArrayList<>());
            userService.addUser(user);}
        List<User> users = userService.getUsers();
        assertNotNull(users, "User list should not be null");
        assertEquals(100, users.size(), "Should return 100 users");
    }

/// // Get the User’s Orders
/// Add a user with no orders and verify that the method returns an empty list.
    @Test
    void testGetOrdersByUserId_ValidUserWithNoOrders() {
        // Arrange: Create a user with no orders
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Test User", new ArrayList<>());
        userService.addUser(user);
        // Act: Retrieve orders for the user
        List<Order> retrievedOrders = userService.getOrdersByUserId(userId);

        // Assert: Verify that the returned list is empty
        assertNotNull(retrievedOrders, "Orders list should not be null");
        assertTrue(retrievedOrders.isEmpty(), "Orders list should be empty");
    }



    ////Verify that the method returns an empty list for a user ID that does not exist.
    @Test
    void testGetOrdersByUserId_InvalidUser() {
        // Arrange: Use a random UUID that does not correspond to any user
        UUID invalidUserId = UUID.randomUUID();

        // Act: Retrieve orders for the invalid user ID
        List<Order> retrievedOrders = userService.getOrdersByUserId(invalidUserId);

        // Assert: Verify that the returned list is empty
        assertNotNull(retrievedOrders, "Orders list should not be null");
        assertTrue(retrievedOrders.isEmpty(), "Orders list should be empty for invalid user ID");
    }
    /// Verify that the method handles a null user ID gracefully (e.g., throws an exception or returns an empty list).
    ///
    @Test
    void testGetOrdersByUserId_NullUserId() {
        // Act and Assert: Verify that the method throws an exception or returns an empty list
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getOrdersByUserId(null);
        });

        assertEquals("User ID cannot be null", exception.getMessage());
    }




    @Test
    void testGetOrdersByUserId_UserHasOrders() {
        // Arrange: Create a user with orders
        UUID userId = UUID.randomUUID();
        Order order1 = new Order(UUID.randomUUID(), userId, 100.0, new ArrayList<>());
        Order order2 = new Order(UUID.randomUUID(), userId, 200.0, new ArrayList<>());
        List<Order> orders = List.of(order1, order2);

        User user = new User(userId, "Order User", orders);
        userService.addUser(user); // Add user with orders

        // Act: Retrieve orders
        List<Order> retrievedOrders = userService.getOrdersByUserId(userId);

        // Assert: Ensure orders are returned correctly
        assertNotNull(retrievedOrders, "Orders should not be null");
        assertEquals(2, retrievedOrders.size(), "User should have 2 orders");
        assertEquals(100.0, retrievedOrders.get(0).getTotalPrice(), "First order should have correct price");
        assertEquals(200.0, retrievedOrders.get(1).getTotalPrice(), "Second order should have correct price");
    }











    /// cart
    ///
    ///


    @Test
    void testAddOrderToUser_EmptyCart() {

        Cart existingCart = cartService.getCartByUserId(testUserId);
        if (existingCart != null) {
            cartService.deleteCartById(existingCart.getId());
        }

        Cart emptyCart = new Cart(UUID.randomUUID(), testUserId, new ArrayList<>());
        cartRepository.save(emptyCart);

        Cart retrievedCart = cartService.getCartByUserId(testUserId);
        assertNotNull(retrievedCart, "Cart should be saved before calling addOrderToUser()");
        assertEquals(0, retrievedCart.getProducts().size(), "Cart should have zero products");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.addOrderToUser(testUserId);
        });

        assertEquals("Cannot place order with an empty cart", exception.getMessage());
    }



    @Test
    void testAddOrderToUser_EmptyCart_NotDeleted() {
        // Ensure test starts with a clean state
        Cart existingCart = cartService.getCartByUserId(testUserId);
        if (existingCart != null) {
            cartService.deleteCartById(existingCart.getId());
        }

        // Create and save an empty cart
        Cart emptyCart = new Cart(UUID.randomUUID(), testUserId, new ArrayList<>());
        cartRepository.save(emptyCart);

        // Retrieve and validate the empty cart
        Cart retrievedCart = cartService.getCartByUserId(testUserId);
        assertNotNull(retrievedCart, "Cart should exist before calling addOrderToUser()");
        assertEquals(0, retrievedCart.getProducts().size(), "Cart should have zero products");

        // Verify the order placement fails due to the empty cart
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.addOrderToUser(testUserId);
        });

        assertEquals("Cannot place order with an empty cart", exception.getMessage());
    }

/// //Ensures that if the cart is deleted before adding an order, the system correctly prevents order placement.
    @Test
    void testAddOrderToUser_EmptyCart_DeletedBeforeAdding() {
        // Ensure any existing cart is removed
        Cart existingCart = cartService.getCartByUserId(testUserId);
        if (existingCart != null) {
            cartService.deleteCartById(existingCart.getId());
        }

        // Create and save an empty cart
        Cart emptyCart = new Cart(UUID.randomUUID(), testUserId, new ArrayList<>());
        cartRepository.save(emptyCart);

        // Retrieve and validate the empty cart
        Cart retrievedCart = cartService.getCartByUserId(testUserId);
        assertNotNull(retrievedCart, "Cart should exist before attempting order placement");
        assertEquals(0, retrievedCart.getProducts().size(), "Cart should have zero products initially");

        // Simulate external deletion of the cart before placing an order
        cartService.deleteCartById(retrievedCart.getId());

        // Attempt to place an order when the cart is no longer available
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.addOrderToUser(testUserId);
        });

        // Verify the error message
        assertEquals("Cart not found for user", exception.getMessage());
    }






    /// delete user


    @Test
    void testDeleteUserById_ExistingUser() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "John Doe", new ArrayList<>());
        userService.addUser(user);

        userService.deleteUserById(userId);

        // ✅ Confirm the user is actually deleted before running assertion
        assertFalse(userService.getUsers().stream().anyMatch(u -> u.getId().equals(userId)),
                "User should be removed from the list");

        // ✅ Expect RuntimeException when trying to retrieve the deleted user
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals("User not found", exception.getMessage());
    }


    @Test
    void testDeleteUserById_UserHasOrders() {
        // Arrange: Create a user with orders
        UUID userId = UUID.randomUUID();
        Order order1 = new Order(UUID.randomUUID(), userId, 100.0, new ArrayList<>());
        Order order2 = new Order(UUID.randomUUID(), userId, 200.0, new ArrayList<>());
        User user = new User(userId, "User with Orders", new ArrayList<>(List.of(order1, order2)));

        userService.addUser(user);

        // Act: Delete the user
        userService.deleteUserById(userId);

        // ✅ Ensure user is deleted
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(userId);
        });
        assertEquals("User not found", exception.getMessage());

        // ✅ Ensure getting orders does NOT throw an exception and returns an empty list
        List<Order> userOrders = assertDoesNotThrow(() -> userService.getOrdersByUserId(userId));
        assertTrue(userOrders.isEmpty(), "User's orders should also be removed");
    }

    @Test
    void testDeleteUserById_NullId() {
        // Act & Assert: Verify that it throws an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUserById(null);
        });

        assertEquals("User ID cannot be null", exception.getMessage());
    }



}


