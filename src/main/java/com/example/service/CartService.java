package com.example.service;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.CartRepository;
import com.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class CartService extends MainService<Cart>{
    private final CartRepository cartRepository;
    private final Cart cart;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, Cart cart, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cart = cart;
        this.userRepository = userRepository;
    }

    public Cart addCart(Cart cart) {
        // Validate input
        if (cart == null) {
            return null;
        }
        UUID userId = cart.getUserId();
        if (userId == null) {
            return null;
        }

        // Check if the user exists
        User user = userRepository.getUserById(userId);
        if (user == null) {
            return null;
        }

        // Check if the user already has a cart
        Cart existingCart = cartRepository.getCartByUserId(userId);
        if (existingCart != null) {
            return existingCart;
        }

        return cartRepository.addCart(cart);
    }
    public ArrayList<Cart> getCarts() {
        return cartRepository.getCarts();
    }

    public Cart getCartById(UUID cartId) {
        if(cartId == null) {
            return null;
        }
        return cartRepository.getCartById(cartId);
    }

    public Cart getCartByUserId(UUID userId) {
        if (userId == null) {
            return null; // Return null instead of throwing an exception
        }
        User user = userRepository.getUserById(userId);
        if (user == null) {
            return null;
        }
        Cart cart = cartRepository.getCartByUserId(userId);

        if (cart == null) {
            return null;
        }

        return cart;
    }


    public void addProductToCart(UUID cartId, Product product) {
        Cart cart = cartRepository.getCartById(cartId);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found with ID: " + cartId);
        }

        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }


        cartRepository.addProductToCart(cartId, product);
    }

    public void deleteProductFromCart(UUID cartId, Product product) {
        Cart cart = cartRepository.getCartById(cartId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found with ID: " + cartId);
        }
        if(product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        cartRepository.deleteProductFromCart(cartId, product);
    }

    public void deleteCartById(UUID cartId) {
        Cart cart = cartRepository.getCartById(cartId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found with ID: " + cartId);
        }
        cartRepository.deleteCartById(cartId);
    }

    public double getTotalPrice(UUID cartId) {
        Cart cart = cartRepository.getCartById(cartId);
        return (cart != null) ? cart.getTotalPrice() : 0.0;
    }


}
