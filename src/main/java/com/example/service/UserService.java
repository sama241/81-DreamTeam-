package com.example.service;

import com.example.model.User;
import com.example.model.Order;
import com.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class UserService  extends MainService {

    private final UserRepository userRepository;
    private final CartService cartService; // Dependency
    private final OrderService orderService; // Dependency

    // 🔹 Dependency Injection: Spring automatically injects UserRepository here
    public UserService(UserRepository userRepository, CartService cartService, OrderService orderService) {
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.orderService = orderService;
    }


    public User addUser(User user) {
        return userRepository.addUser(user);
    }


    public ArrayList<User> getUsers() {
        return userRepository.getUsers();
    }

    public void addOrderToUser(UUID userId) {
        // Checkout process
        emptyCart(userId); // Clears cart after order
        // Add order to user (Assume order object is created earlier)
        orderService.addOrder(new Order(userId, 0.0, new ArrayList<>()));
    }

    public void deleteUserById(UUID userId) {
        userRepository.deleteUserById(userId);
    }
    public User getUserById(UUID userId) {
        return userRepository.getUserById(userId);
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        return userRepository.getOrdersByUserId(userId);
    }

    public void removeOrderFromUser(UUID userId, UUID orderId) {
        userRepository.removeOrderFromUser(userId, orderId);
    }
    public void emptyCart(UUID userId) {
        cartService.deleteCartById(userId);
    }
}
