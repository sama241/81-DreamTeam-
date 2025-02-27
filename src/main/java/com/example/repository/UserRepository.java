package com.example.repository;

import com.example.model.Order;
import com.example.model.User;
import jdk.dynalink.beans.StaticClass;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class UserRepository extends MainRepository<User>{
    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/users.json"; // Path to users.json
    }


    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }

    public User getUserById(UUID userId) {
        return findAll().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        // Get the user from the list
        User user = findAll().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);

        // If user exists, return their orders, otherwise return an empty list
        return (user != null) ? user.getOrders() : new ArrayList<>();
    }


    public void removeOrderFromUser(UUID userId, UUID orderId) {
        // Get all users from JSON
        List<User> users = findAll();

        // Find the user by ID and remove the order
        for (User user : users) {
            if (user.getId().equals(userId)) {
                boolean removed = user.getOrders().removeIf(order -> order.getId().equals(orderId));

                if (!removed) {
                    throw new RuntimeException("Order ID not found for the user.");
                }
                break;
            }
        }

        // Write the updated list back to users.json
        try {
            writeToJson(users);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update users.json: " + e.getMessage());
        }
    }



}