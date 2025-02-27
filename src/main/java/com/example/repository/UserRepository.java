package com.example.repository;

import com.example.model.User;
import com.example.model.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
public class UserRepository extends MainRepository<User> {

    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/users.json"; // JSON file path for users
    }

    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }

    // 1️⃣ Get All Users
    public ArrayList<User> getUsers() {
        return findAll();
    }

    // 3️⃣ Add New User
    public User addUser(User user) {
        user.setId(UUID.randomUUID()); // Ensure the user has a unique ID
        save(user); // Uses MainRepository's save() to write to JSON
        return user;
    }

    // 5️⃣ Add Order to a User
    public void addOrderToUser(UUID userId, Order order) {
        ArrayList<User> users = getUsers(); // Get the list of users
        for (User user : users) {
            if (user.getId().equals(userId)) {
                user.getOrders().add(order); // Add the order to the user's list
                break;
            }
        }
        overrideData(users); // Save the updated user data
    }

    // 7️⃣ Delete User by ID
    public void deleteUserById(UUID userId) {
        ArrayList<User> users = getUsers(); // Get all users
        users.removeIf(user -> user.getId().equals(userId)); // Remove the matching user
        overrideData(users); // Save the updated user list
    }
}
