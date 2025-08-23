package com.interstore.interstore_backend.service;

import com.interstore.interstore_backend.entity.Cart;
import com.interstore.interstore_backend.entity.CartItem;
import com.interstore.interstore_backend.entity.Prodotto;
import com.interstore.interstore_backend.repository.CartRepository;
import com.interstore.interstore_backend.repository.CartItemRepository;
import com.interstore.interstore_backend.repository.ProdottoRepository;
import com.interstore.interstore_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProdottoRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProdottoRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Optional<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Long cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }

    @Transactional
    public Cart addProductToCart(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(userRepository.findById(userId).orElseThrow());
                    return cartRepository.save(newCart);
                });

        Prodotto product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProdotto().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProdotto(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeProductFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrello non trovato"));

        cart.getItems().removeIf(item -> item.getProdotto().getId().equals(productId));

        return cartRepository.save(cart);
    }

    // 🔹 Aggiorna quantità di un prodotto nel carrello
    @Transactional
    public Cart updateProductQuantity(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrello non trovato"));

        cart.getItems().forEach(item -> {
            if (item.getProdotto().getId().equals(productId)) {
                item.setQuantity(quantity);
            }
        });

        return cartRepository.save(cart);
    }
}
