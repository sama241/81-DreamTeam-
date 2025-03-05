package com.example.service;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.model.User;
import com.example.model.Order;
import com.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
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

    // 🔹 Dependency Injection: Spring automatically injects UserRepository here
    public UserService(UserRepository userRepository, CartService cartService, OrderService orderService, ProductService productService, Cart cart) {
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.orderService = orderService;
        this.productService = productService;
        this.cart = cart;
    }


    public User addUser(User user) {
        return userRepository.addUser(user);
    }


    public ArrayList<User> getUsers() {
        return (ArrayList<User>) userRepository.getUsers();
    }

    public void addOrderToUser(UUID userId) {
        System.out.println(userId);
        Cart cart=cartService.getCartByUserId(userId);
        System.out.println(cart);
        Order order=new Order(userId,cart.getTotalPrice(), cart.getProducts());
        orderService.addOrder(order);
        cartService.deleteCartById(cart.getId()); // Clears cart after order
        userRepository.addOrderToUser(userId,order);
    }

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
    public void emptyCart(UUID userId) {

        Cart cart=cartService.getCartByUserId(userId);
        cartService.deleteCartById(cart.getId());

    }
    public void addProductToCart(UUID userId, UUID productId){
       Cart cart= cartService.getCartByUserId(userId);
       System.out.println("ana hena 2");
       System.out.println(cart.getId());
        Product product=productService.getProductById(productId);
        if(cart == null){
            cart  = new Cart(userId,new ArrayList<>());
            cartService.addCart(cart);
        }
        cartService.addProductToCart(cart.getId(),product);
    }
    public Boolean deleteProductFromCart(UUID userId, UUID productId) {
        Cart cart = cartService.getCartByUserId(userId);

        boolean productExists = cart.getProducts().stream()
                .anyMatch(p -> p.getId().equals(productId));

        if (!productExists) {
            return false;
        }

        cart.getProducts().removeIf(p -> p.getId().equals(productId));
        return true; // ✅ Product successfully removed
    }


}
