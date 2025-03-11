package com.example.MiniProject1;

import com.example.model.Order;
import com.example.model.Product;
import com.example.repository.OrderRepository;
import com.example.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    @Value("${spring.application.orderDataPath}")
    private String DATA_PATH;


    @BeforeEach
    void cleanOrdersFile() throws IOException {

        objectMapper.writeValue(new File(DATA_PATH), new ArrayList<Order>());
    }

    /// /////////// addOrder Tests //////////////////////

    @Test
    void testPreventAddingOrderWithoutUserId() throws IOException {

        Order invalidOrder = new Order(UUID.randomUUID(), null, 49.99, List.of(new Product(UUID.randomUUID(), "Test Product", 19.99)));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.addOrder(invalidOrder);
        });

        assertEquals("User ID cannot be null!", exception.getMessage());

        List<Order> savedOrders = objectMapper.readValue(new File(DATA_PATH), objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));

        assertTrue(savedOrders.isEmpty(), "No order should be saved in JSON when userId is null");
    }

    @Test
    void testPreventAddingOrderWithNegativePrice() throws IOException {

        Order invalidOrder = new Order(UUID.randomUUID(), UUID.randomUUID(), -10.00, List.of(new Product(UUID.randomUUID(), "Test Product", 19.99)));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.addOrder(invalidOrder);
        });

        assertEquals("Total price must be greater than zero!", exception.getMessage());

        List<Order> savedOrders = objectMapper.readValue(new File(DATA_PATH), objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));

        assertTrue(savedOrders.isEmpty(), "No order should be saved in JSON when total price is negative or zero");
    }

    @Test
    void testOrderIsSavedSuccessfully() throws IOException {

        Order newOrder = new Order(UUID.randomUUID(), UUID.randomUUID(), 79.99, List.of(new Product(UUID.randomUUID(), "Test Product", 19.99)));

        orderService.addOrder(newOrder);

        List<Order> savedOrders = objectMapper.readValue(new File(DATA_PATH), objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));

        assertFalse(savedOrders.isEmpty(), "Orders list should not be empty after adding an order");
        assertTrue(savedOrders.stream().anyMatch(order -> order.getId().equals(newOrder.getId())), "The newly added order should exist in JSON");

    }





    ///////////// get orders tests ///////////////////

    @Test
    void testGetOrdersReturnsEmptyListWhenNoOrdersExist() throws IOException {

        objectMapper.writeValue(new File(DATA_PATH), new ArrayList<Order>());

        ArrayList<Order> retrievedOrders = orderService.getOrders();

        System.out.println("Retrieved Orders: " + retrievedOrders);

        assertNotNull(retrievedOrders, "getOrders() should not return null");
        assertTrue(retrievedOrders.isEmpty(), "getOrders() should return an empty list when no orders exist");
    }

    @Test
    void testGetOrdersReturnsCorrectNumberOfOrders() throws IOException {
        objectMapper.writeValue(new File(DATA_PATH), new ArrayList<Order>());

        Order order1 = new Order(UUID.randomUUID(), UUID.randomUUID(), 50.0, List.of(new Product(UUID.randomUUID(), "Product A", 10.0)));
        Order order2 = new Order(UUID.randomUUID(), UUID.randomUUID(), 100.0, List.of(new Product(UUID.randomUUID(), "Product B", 20.0)));
        Order order3 = new Order(UUID.randomUUID(), UUID.randomUUID(), 150.0, List.of(new Product(UUID.randomUUID(), "Product C", 30.0)));

        orderService.addOrder(order1);
        orderService.addOrder(order2);
        orderService.addOrder(order3);

        ArrayList<Order> retrievedOrders = orderService.getOrders();

        System.out.println("Retrieved Orders: " + retrievedOrders);

        assertNotNull(retrievedOrders, "getOrders() should not return null");
        assertEquals(3, retrievedOrders.size(), "getOrders() should return exactly 3 orders");
    }

    @Test
    void testGetOrdersDoesNotModifyData() throws IOException {

        objectMapper.writeValue(new File(DATA_PATH), new ArrayList<Order>());

        Order order1 = new Order(UUID.randomUUID(), UUID.randomUUID(), 50.0, List.of(new Product(UUID.randomUUID(), "Product A", 10.0)));
        Order order2 = new Order(UUID.randomUUID(), UUID.randomUUID(), 100.0, List.of(new Product(UUID.randomUUID(), "Product B", 20.0)));

        orderService.addOrder(order1);
        orderService.addOrder(order2);

        List<Order> firstRetrieval = orderService.getOrders();

        List<Order> secondRetrieval = orderService.getOrders();

        System.out.println("First Retrieval IDs: " + firstRetrieval.stream().map(Order::getId).toList());
        System.out.println("Second Retrieval IDs: " + secondRetrieval.stream().map(Order::getId).toList());

        // Assertions
        assertNotNull(firstRetrieval, "First getOrders() call should not return null");
        assertNotNull(secondRetrieval, "Second getOrders() call should not return null");
        assertEquals(firstRetrieval.size(), secondRetrieval.size(), "Both retrievals should return the same number of orders");

        List<UUID> firstRetrievalIds = firstRetrieval.stream().map(Order::getId).toList();
        List<UUID> secondRetrievalIds = secondRetrieval.stream().map(Order::getId).toList();

        assertEquals(firstRetrievalIds, secondRetrievalIds, "Both retrievals should contain the same orders based on IDs");
    }



    /// //////////////////// getorderbyID  Tests //////////////////////



    @Test
    void testGetOrderByIdReturnsNullForNonExistentOrder() throws IOException {

        objectMapper.writeValue(new File(DATA_PATH), new ArrayList<Order>());

        UUID nonExistentOrderId = UUID.randomUUID();

        Order retrievedOrder = orderService.getOrderById(nonExistentOrderId);

        System.out.println("Retrieved Order for Non-Existent ID: " + retrievedOrder);

        // Assertions
        assertNull(retrievedOrder, "getOrderById() should return null if the order does not exist");
    }

    @Test
    void testGetOrderByIdReturnsNullAfterOrderIsDeleted() throws IOException {

        objectMapper.writeValue(new File(DATA_PATH), new ArrayList<Order>());

        UUID orderId = UUID.randomUUID();
        Order testOrder = new Order(orderId, UUID.randomUUID(), 99.99, List.of(new Product(UUID.randomUUID(), "Test Product", 19.99)));

        orderService.addOrder(testOrder);

        Order retrievedOrderBeforeDelete = orderService.getOrderById(orderId);
        assertNotNull(retrievedOrderBeforeDelete, "Order should exist before deletion");

        orderService.deleteOrderById(orderId);

        Order retrievedOrderAfterDelete = orderService.getOrderById(orderId);

        System.out.println("Retrieved Order After Deletion: " + retrievedOrderAfterDelete);

        assertNull(retrievedOrderAfterDelete, "getOrderById() should return null after the order has been deleted");
    }

    @Test
    void testGetOrderByIdReturnsNullForNonExistentId() {

        UUID nonExistentOrderId = UUID.randomUUID();

        Order retrievedOrder = orderService.getOrderById(nonExistentOrderId);

        assertNull(retrievedOrder, "getOrderById() should return null for a non-existent order ID");
    }




    /// /////////     deleteorderbyID Tests    ///////////////////////////
    ///
    @Test
    void testDeleteOrderByIdSuccessfullyRemovesOrder() throws IOException {

        objectMapper.writeValue(new File(DATA_PATH), new ArrayList<Order>());

        UUID orderId = UUID.randomUUID();
        Order testOrder = new Order(orderId, UUID.randomUUID(), 99.99, List.of(new Product(UUID.randomUUID(), "Test Product", 19.99)));
        orderService.addOrder(testOrder);

        assertNotNull(orderService.getOrderById(orderId), "Order should exist before deletion");

        orderService.deleteOrderById(orderId);

        Order retrievedOrderAfterDelete = orderService.getOrderById(orderId);

        System.out.println("Retrieved Order After Deletion: " + retrievedOrderAfterDelete);

        assertNull(retrievedOrderAfterDelete, "getOrderById() should return null after the order has been deleted");
    }
    @Test
    void testDeleteOrderByIdThrowsExceptionForNonExistentOrder() {

        UUID nonExistentOrderId = UUID.randomUUID();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.deleteOrderById(nonExistentOrderId);
        });

        assertEquals("Order with ID " + nonExistentOrderId + " not found!", exception.getMessage());
    }

    @Test
    void testDeleteLastRemainingOrderResultsInEmptyList() throws IOException {

        objectMapper.writeValue(new File(DATA_PATH), new ArrayList<Order>());


        UUID orderId = UUID.randomUUID();
        Order singleOrder = new Order(orderId, UUID.randomUUID(), 75.0, List.of(new Product(UUID.randomUUID(), "Single Product", 15.0)));
        orderService.addOrder(singleOrder);

        assertNotNull(orderService.getOrderById(orderId), "Order should exist before deletion");

        orderService.deleteOrderById(orderId);

        List<Order> remainingOrders = orderService.getOrders();

        System.out.println("Remaining Orders After Deleting Last Order: " + remainingOrders);

        // Assertions
        assertNotNull(remainingOrders, "getOrders() should return an empty list, not null");
        assertTrue(remainingOrders.isEmpty(), "After deleting the last order, the order list should be empty");
    }

}
