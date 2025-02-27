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

    // 🔹 Dependency Injection: Spring automatically injects UserRepository here
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1️⃣ Add New User
    public User addUser(User user) {
        return userRepository.addUser(user);
    }

    // 3️⃣ Get All Users
    public ArrayList<User> getUsers() {
        return userRepository.getUsers();
    }

    // 5️⃣ Add Order to User
    public void addOrderToUser(UUID userId, Order order) {
        userRepository.addOrderToUser(userId, order);
    }

    // 7️⃣ Delete User by ID
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
}
