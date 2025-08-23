package com.interstore.interstore_backend.controller;

import com.interstore.interstore_backend.entity.Cart;
import com.interstore.interstore_backend.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable Long userId) {
        Optional<Cart> cart = cartService.getCartByUserId(userId);
        return cart.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Cart saveCart(@RequestBody Cart cart) {
        return cartService.saveCart(cart);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<Cart> addProductToCart(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {

        Cart updatedCart = cartService.addProductToCart(userId, productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<Cart> removeProductFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        Cart updatedCart = cartService.removeProductFromCart(userId, productId);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/{userId}/update/{productId}")
    public ResponseEntity<Cart> updateProductQuantity(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam int quantity) {

        Cart updatedCart = cartService.updateProductQuantity(userId, productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

}

