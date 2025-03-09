package com.example.controller;

import com.example.model.Cart;
import com.example.model.User;
import com.example.model.Order;
import com.example.repository.CartRepository;
import com.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CartRepository cartRepository;
    // 🔹 Dependency Injection: Spring automatically injects UserService
    public UserController(UserService userService , CartRepository cartRepository) {
        this.userService = userService;
        this.cartRepository = cartRepository;
    }

    @PostMapping("/")
    public User addUser(@RequestBody User user) {
        try {
            return userService.addUser(user);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return null; //
        }
    }


    @GetMapping("/")
    public ArrayList<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/{userId}/checkout")
    public String addOrderToUser(@PathVariable UUID userId) {
        try {
            userService.addOrderToUser(userId);
            return "Order added successfully";
        } catch (RuntimeException e) {
            return "Error: " + e.getMessage();
        }
    }

    @DeleteMapping("/delete/{userId}")
    public String deleteUserById(@PathVariable UUID userId) {
        List<User> users = userService.getUsers(); // ✅ Fetch all users first

        boolean userExists = users.stream().anyMatch(user -> user.getId().equals(userId));

        if (!userExists) {
            return"User not found";
        }

        userService.deleteUserById(userId); // ✅ Now call the service method
        return "User deleted successfully";
    }
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable UUID userId) {
        try {
            return userService.getUserById(userId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error: " + e.getMessage()); // Handles null userId
        } catch (RuntimeException e) {
            throw new RuntimeException("Error: " + e.getMessage()); // Handles user not found
        }
    }

    @GetMapping("/{userId}/orders")
    public List<Order> getOrdersByUserId(@PathVariable UUID userId) {
        return userService.getOrdersByUserId(userId);
    }

    @PostMapping("/{userId}/removeOrder")
    public String removeOrderFromUser(@PathVariable UUID userId, @RequestParam UUID orderId) {
        try {
            userService.removeOrderFromUser(userId, orderId);
            return "Order removed successfully";
        } catch (RuntimeException e) {
            return "Error: " + e.getMessage();
        }
    }
    @DeleteMapping("/{userId}/emptyCart")
    public String emptyCart(@PathVariable UUID userId){
        userService.emptyCart(userId);
        return "Cart emptied successfully";
    }

    @PutMapping("/addProductToCart")
    public String addProductToCart(@RequestParam UUID userId, @RequestParam UUID productId) {
        userService.addProductToCart(userId, productId);
        return "Product added to cart";
    }

    @PutMapping("/deleteProductFromCart")
    public String deleteProductFromCart(@RequestParam UUID userId, @RequestParam UUID productId) {
        // Retrieve the cart for the user
        Cart cart = cartRepository.getCartByUserId(userId);

        if (cart == null) {
            return "Cart is empty";
        }

        boolean productExists = cart.getProducts().stream()
                .anyMatch(p -> p.getId().equals(productId));

        if (!productExists) {
            return "Product not found in cart";
        }

        // Remove the product
        cart.getProducts().removeIf(p -> p.getId().equals(productId));

        cartRepository.save(cart);
        return "Product deleted from cart";
    }

}
