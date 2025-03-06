package com.example.service;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.model.User;
import com.example.model.Order;
import com.example.repository.CartRepository;
import com.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class UserService  extends MainService {

    private final UserRepository userRepository;
    private final CartService cartService; // Dependency
    private final OrderService orderService; // Dependency
    private final ProductService productService;
    private final Cart cart;
    private  final CartRepository cartRepository;

    // 🔹 Dependency Injection: Spring automatically injects UserRepository here
    public UserService(UserRepository userRepository, CartService cartService, OrderService orderService, ProductService productService, Cart cart,CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.orderService = orderService;
        this.productService = productService;
        this.cart = cart;
        this.cartRepository=cartRepository;
    }


    public User addUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new RuntimeException("User name cannot be empty");
        }
        if (userRepository.getUserById(user.getId()) != null) {
            throw new RuntimeException("User with this ID already exists");
        }

        return userRepository.addUser(user);
    }

    public ArrayList<User> getUsers() {
        return (ArrayList<User>) userRepository.getUsers();
    }

    public void addOrderToUser(UUID userId) {
        Cart cart = cartService.getCartByUserId(userId);

        if (cart == null) {
            throw new RuntimeException("Cart not found for user");
        }
        if (cart.getProducts().isEmpty()) {
            throw new RuntimeException("Cannot place order with an empty cart");
        }

        Order order = new Order(userId, cart.getTotalPrice(), cart.getProducts());
        orderService.addOrder(order);
        userRepository.addOrderToUser(userId, order);
        cartService.deleteCartById(cart.getId());

        System.out.println("Order added to user: " + userRepository.getOrdersByUserId(userId));
    }


    public void deleteUserById(UUID userId) {
        userRepository.deleteUserById(userId);
    }
    public User getUserById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        return userRepository.getOrdersByUserId(userId);
    }

    public void removeOrderFromUser(UUID userId, UUID orderId) {
        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        boolean removed = user.getOrders().removeIf(order -> order.getId().equals(orderId));

        if (!removed) {
            throw new RuntimeException("Order not found for user");
        }

        userRepository.removeOrderFromUser(userId, orderId);
    }


    public void emptyCart(UUID userId) {
        Cart cart = cartService.getCartByUserId(userId);

          UUID oldID= cart.getId();
          cartService.deleteCartById(cart.getId());

            // ✅ Create a new empty cart for the user
            Cart newCart = new Cart(oldID, userId, new ArrayList<>());
            cartService.addCart(newCart);

    }
    public void addProductToCart(UUID userId, UUID productId){
       Cart cart= cartService.getCartByUserId(userId);
        Product product=productService.getProductById(productId);
        if(cart == null){
            cart  = new Cart(userId,new ArrayList<>());
            cartService.addCart(cart);
        }
        cartService.addProductToCart(cart.getId(),product);
    }

}
