package com.dmdm.ecommerce_backend.Controllers;

import com.dmdm.ecommerce_backend.Dto.CartItem.CartItemRequestDTO;
import com.dmdm.ecommerce_backend.Dto.CartItem.CartItemResponseDTO;
import com.dmdm.ecommerce_backend.Services.CartItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<Void> addToCart(@RequestBody CartItemRequestDTO dto, HttpServletRequest request) {
        cartItemService.addItemToCart(dto, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponseDTO>> getCartItems(HttpServletRequest request) {
        return ResponseEntity.ok(cartItemService.getCartItems(request));
    }

    @GetMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponseDTO> getCartItem(@PathVariable Long cartItemId, HttpServletRequest request) {
        return ResponseEntity.ok(cartItemService.getCartItem(cartItemId, request));
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<Void> updateQuantity(@PathVariable Long cartItemId,
                                               @RequestParam int quantity,
                                               HttpServletRequest request) {
        cartItemService.updateQuantity(cartItemId, quantity, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long cartItemId, HttpServletRequest request) {
        cartItemService.removeItem(cartItemId, request);
        return ResponseEntity.ok().build();
    }
}
