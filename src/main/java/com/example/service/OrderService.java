package com.example.service;

import com.example.model.Order;
import com.example.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class OrderService extends MainService<Order> {

    private final OrderRepository orderRepository;

    // Constructor Injection for Dependency Injection
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 7.5.2.1 Add Order
    public void addOrder(Order order) {
        try {
            orderRepository.addOrder(order);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation Error: " + e.getMessage());
            throw e; // Rethrow so the test can detect it
        } catch (RuntimeException e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            throw e; // Rethrow to allow the test to fail properly
        }
    }


    // 7.5.2.2 Get All Orders
    public ArrayList<Order> getOrders() {
        try {
            return orderRepository.getOrders();
        } catch (Exception e) {
            System.err.println("Error retrieving orders: " + e.getMessage());
            return new ArrayList<>(); // Return an empty list if an error occurs
        }
    }

    // 7.5.2.3 Get a Specific Order
    public Order getOrderById(UUID orderId) {
        try {
            Order order = orderRepository.getOrderById(orderId);
            if (order == null) {
                throw new IllegalArgumentException("Order with ID " + orderId + " not found!");
            }
            return order;
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return null; // Returning null if the order is not found
        } catch (Exception e) {
            System.err.println("Unexpected error occurred while retrieving order: " + e.getMessage());
            return null;
        }
    }


    // 7.5.2.4 Delete a Specific Order
    public void deleteOrderById(UUID orderId) throws IllegalArgumentException {
        try {
            Order order = getOrderById(orderId);
            if (order != null) {
                orderRepository.deleteOrderById(orderId);
                System.out.println("Order with ID " + orderId + " successfully deleted.");
            } else {
                throw new IllegalArgumentException("Order with ID " + orderId + " not found!");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            throw e; // Rethrow so tests can detect it
        } catch (RuntimeException e) {
            System.err.println("Unexpected error while deleting order: " + e.getMessage());
            throw e; // Ensure unexpected errors are also rethrown
        }
    }

}
