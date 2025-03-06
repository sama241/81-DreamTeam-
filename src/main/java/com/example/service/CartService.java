package com.example.service;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class CartService extends MainService<Cart>{
    private final CartRepository cartRepository;
    private final Cart cart;

    public CartService(CartRepository cartRepository, Cart cart) {
        this.cartRepository = cartRepository;
        this.cart = cart;
    }

    public Cart addCart(Cart cart) {
        Cart existingCart = cartRepository.getCartByUserId(cart.getUserId());
        if (existingCart != null) {
            return existingCart;
        }
        return cartRepository.addCart(cart);
    }

    public ArrayList<Cart> getCarts() {
        return cartRepository.getCarts();
    }

    public Cart getCartById(UUID cartId) {
        return cartRepository.getCartById(cartId);
    }

    public Cart getCartByUserId(UUID userId) {
        Cart cart = cartRepository.getCartByUserId(userId);

        return cart;
    }


    public void addProductToCart(UUID cartId, Product product) {
        cartRepository.addProductToCart(cartId, product);
    }

    public void deleteProductFromCart(UUID cartId, Product product) {
        Cart cart = cartRepository.getCartById(cartId);
        if (cart == null) {
            System.out.println("Cannot delete product. Cart not found with ID: " + cartId);
            return;
        }
        cartRepository.deleteProductFromCart(cartId, product);
    }

    public void deleteCartById(UUID cartId) {
        Cart cart = cartRepository.getCartById(cartId);
        if (cart == null) {
            System.out.println("Cannot delete cart. Cart not found with ID: " + cartId);
            return;
        }
        cartRepository.deleteCartById(cartId);
    }

    public double getTotalPrice(UUID cartId) {
        Cart cart = cartRepository.getCartById(cartId);
        return (cart != null) ? cart.getTotalPrice() : 0.0;
    }


}
