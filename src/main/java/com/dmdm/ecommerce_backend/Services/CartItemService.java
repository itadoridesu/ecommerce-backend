package com.dmdm.ecommerce_backend.Services;


import com.dmdm.ecommerce_backend.Config.JwtService;
import com.dmdm.ecommerce_backend.Dto.CartItem.CartItemRequestDTO;
import com.dmdm.ecommerce_backend.Dto.CartItem.CartItemResponseDTO;
import com.dmdm.ecommerce_backend.Entity.CartItem;
import com.dmdm.ecommerce_backend.Entity.Products.Product;
import com.dmdm.ecommerce_backend.Entity.User;
import com.dmdm.ecommerce_backend.Mapper.CartItemMapper;
import com.dmdm.ecommerce_backend.Repositories.CartItemRepository;
import com.dmdm.ecommerce_backend.Repositories.Products.ProductRepository;
import com.dmdm.ecommerce_backend.Repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CartItemMapper cartItemMapper;


    private User getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtService.extractUsername(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void addItemToCart(CartItemRequestDTO dto, HttpServletRequest request) {
        User user = getCurrentUser(request);
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository.findByUserAndProductId(user, product.getId())
                .map(existingItem -> {
                    existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
                    return existingItem;
                })
                .orElse(CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(dto.getQuantity())
                        .build());

        cartItemRepository.save(cartItem);
    }

    public List<CartItemResponseDTO> getCartItems(HttpServletRequest request) {
        User user = getCurrentUser(request);
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        return cartItemMapper.toDtoList(cartItems); //  Use mapper now
    }

    public CartItemResponseDTO getCartItem(Long id, HttpServletRequest request) {
        User user = getCurrentUser(request);
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        return cartItemMapper.toDto(cartItem); //  Use mapper now
    }

    public void updateQuantity(Long cartItemId, int newQuantity, HttpServletRequest request) {
        User user = getCurrentUser(request);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getUser().getId().equals(user.getId()))
            throw new RuntimeException("Access denied");

        item.setQuantity(newQuantity);
        cartItemRepository.save(item);
    }

    public void removeItem(Long cartItemId, HttpServletRequest request) {
        User user = getCurrentUser(request);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getUser().getId().equals(user.getId()))
            throw new RuntimeException("Access denied");

        cartItemRepository.delete(item);
    }
}
