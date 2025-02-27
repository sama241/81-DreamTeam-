package com.example.repository;
import com.example.model.Order;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class OrderRepository extends MainRepository<Order> {

    @Override
    protected String getDataPath() {
        return "src/main/java/com/example/data/orders.json"; // Ensure correct path
    }

    @Override
    protected Class<Order[]> getArrayType() {
        return Order[].class;
    }

    public OrderRepository() {}

    // 6.5.2.1 Add Order with Unique ID
    public Order addOrder(Order order) {
        order.setId(UUID.randomUUID()); // Generate a unique ID for the order
        save(order); // Save order to JSON
        return order;
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
