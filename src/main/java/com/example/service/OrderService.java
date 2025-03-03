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
        orderRepository.addOrder(order);
    }

    // 7.5.2.2 Get All Orders
    public ArrayList<Order> getOrders() {
        return orderRepository.getOrders();
    }

    // 7.5.2.3 Get a Specific Order
    public Order getOrderById(UUID orderId) {
        return orderRepository.getOrderById(orderId);
    }

    // 7.5.2.4 Delete a Specific Order
    public void deleteOrderById(UUID orderId) {
        Order order = getOrderById(orderId);
        if (order != null) {
            orderRepository.deleteOrderById(orderId);
        } else {
            throw new IllegalArgumentException("Order with ID " + orderId + " not found!");
        }
    }
}
