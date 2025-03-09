package com.example.repository;

import com.example.model.User;
import com.example.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository extends MainRepository<User> {
    @Value("${spring.application.userDataPath}")
    private String userDataPath;
    @Override
    protected String getDataPath() {
        return userDataPath; // JSON file path for users
    }

    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }

    // 1️⃣ Get All Users
    public List<User> getUsers() {

        return findAll();
    }

    // 3️⃣ Add New User
    public User addUser(User user) {
        if (user.getId() == null) {  // ✅ Only generate an ID if it's missing
            user.setId(UUID.randomUUID());
        }
        save(user);
        return user;
    }


    // 5️⃣ Add Order to a User
    public void addOrderToUser(UUID userId, Order order) {

        ArrayList<User> users = (ArrayList<User>) getUsers(); // Get the list of users
        for (User user : users) {
            if (user.getId().equals(userId)) {
                user.getOrders().add(order); // Add the order to the user's list
                break;
            }
        }
        overrideData(users); // Save the updated user data
    }




    public void deleteUserById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        // Get the existing list of users
            ArrayList<User> users = (ArrayList<User>) getUsers(); // Get the list of users

        boolean removed = users.removeIf(user -> user.getId().equals(userId));

        if (!removed) {
            throw new RuntimeException("User not found");
        }

        overrideData(users); // ✅ Ensure persistence
    }



    public User getUserById(UUID userId) {
    return findAll().stream()
            .filter(user -> user.getId().equals(userId))
            .findFirst()
            .orElse(null);
}

    public List<Order> getOrdersByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        User user = findAll().stream()
                .filter(u -> userId.equals(u.getId()))
                .findFirst()
                .orElse(null);

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

    public void clearUsers() {
        overrideData(new ArrayList<>()); // 🚀 Clears the JSON data by replacing it with an empty list
    }

}