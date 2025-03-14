package com.example.controller;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    @PostMapping("/")
    public Cart addCart(@RequestBody Cart cart) {
        return cartService.addCart(cart);  // Calls the Service layer
    }

    @GetMapping("/")
    public ArrayList<Cart> getCarts() {
        return cartService.getCarts();
    }

    @GetMapping("/{cartId}")
    public Cart getCartById(@PathVariable UUID cartId) {
        return cartService.getCartById(cartId);
    }

    @GetMapping("/user/{userId}")
    public Cart getCartByUserId(@PathVariable UUID userId) {
        return cartService.getCartByUserId(userId);
    }

    @PutMapping("/addProduct/{cartId}")
    public String addProductToCart(@PathVariable UUID cartId, @RequestBody Product product) {
        try {
            cartService.addProductToCart(cartId, product);
            return "Product added to cart successfully";
        } catch (IllegalArgumentException e) {
            return "Cart not found";
        }

    }

    @DeleteMapping("/deleteProduct/{cartId}")
    public String deleteProductFromCart(@PathVariable UUID cartId, @RequestBody Product product) {
        try {
            cartService.deleteProductFromCart(cartId, product);
            return "Product deleted from cart";
        } catch (IllegalArgumentException e) {
            return "Cart not found";
        }
    }

    @DeleteMapping("/delete/{cartId}")
    public String deleteCartById(@PathVariable UUID cartId) {
        try{
            cartService.deleteCartById(cartId);
            return "Cart deleted successfully";
        }catch (IllegalArgumentException e) {
            return "Cart not found";
        }
    }

    @GetMapping("/total/{cartId}")
    public double getTotalPrice(@PathVariable UUID cartId) {
        return cartService.getTotalPrice(cartId);
    }












}
