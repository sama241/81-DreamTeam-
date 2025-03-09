package com.example.repository;
import com.example.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class OrderRepository extends MainRepository<Order> {
    @Value("${spring.application.orderDataPath}")
    private String orderDataPath;
    @Override
    protected String getDataPath() {
        return orderDataPath; // Ensure correct path
    }

    @Override
    protected Class<Order[]> getArrayType() {
        return Order[].class;
    }

    public OrderRepository() {}

    // 6.5.2.1 Add Order with Unique ID
    public void addOrder(Order order) {

        if (order.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null!");
        }
        // Validate that the total price is greater than zero
        if (order.getTotalPrice() <= 0) {
            throw new IllegalArgumentException("Total price must be greater than zero!");
        }

        // Validate that the order has at least one product

        // Generate a unique ID if not provided
        if(order.getId() == null) {
            order.setId(UUID.randomUUID());
        }

        // Save the order
        save(order);

    }


    // 6.5.2.2 Get All Orders
    public ArrayList<Order> getOrders() {
        return findAll(); // Calls findAll() from MainRepository
    }

    // 6.5.2.3 Get a Specific Order by ID
    public Order getOrderById(UUID orderId) {
        return getOrders().stream()
                .filter(order -> order.getId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    // 6.5.2.4 Delete a Specific Order
    public void deleteOrderById(UUID orderId) {
        ArrayList<Order> orders = getOrders();
        orders.removeIf(order -> order.getId().equals(orderId));
        overrideData(orders); // Updates JSON file after deletion
    }


}
