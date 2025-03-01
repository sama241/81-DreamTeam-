package com.example.controller;

import com.example.model.User;
import com.example.model.Order;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 🔹 Dependency Injection: Spring automatically injects UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/")
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @GetMapping("/")
    public ArrayList<User> getUsers() {
        return userService.getUsers();
    }


    @PostMapping("/{userId}/checkout")
    public String addOrderToUser(@PathVariable UUID userId){
        userService.addOrderToUser(userId);
        return "Order added successfully for user ID: " + userId;
    }

    @DeleteMapping("/delete/{userId}")
    public String deleteUserById(@PathVariable UUID userId) {
        userService.deleteUserById(userId);
        return "User deleted successfully with ID: " + userId;
    }
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable UUID userId) {
        return userService.getUserById(userId);
    }


    @GetMapping("/{userId}/orders")
    public List<Order> getOrdersByUserId(@PathVariable UUID userId) {
        return userService.getOrdersByUserId(userId);
    }

    @PostMapping("/{userId}/removeOrder")
    public String removeOrderFromUser(@PathVariable UUID userId, @RequestParam UUID orderId) {
        userService.removeOrderFromUser(userId, orderId);
        return "Order removed successfully!";
    }
    @DeleteMapping("/{userId}/emptyCart")
    public String emptyCart(@PathVariable UUID userId){
        userService.emptyCart(userId);
        return "Cart is empty";
    }
}
