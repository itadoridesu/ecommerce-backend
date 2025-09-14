package com.dmdm.ecommerce_backend.Mapper;

import com.dmdm.ecommerce_backend.Dto.CartItem.CartItemResponseDTO;
import com.dmdm.ecommerce_backend.Entity.CartItem;
import java.util.List;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class CartItemMapper {

    private final ProductMapper productMapper;

    public CartItemMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public CartItemResponseDTO toDto(CartItem cartItem) {
        if (cartItem == null) return null;

        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setId(cartItem.getId());
        dto.setQuantity(cartItem.getQuantity());
        dto.setProduct(productMapper.toDTO(cartItem.getProduct()));

        return dto;
    }

    public List<CartItemResponseDTO> toDtoList(List<CartItem> cartItems) {
        if (cartItems == null) return null;
        return cartItems.stream().map(this::toDto).collect(Collectors.toList());
    }
}


//@Mapper(componentModel = "spring", uses = ProductMapper.class)
//public interface CartItemMapper {
//
//    //  @Mapping(source = "product", target = "product") // product → ProductDTO
//    @Mapping(source = "product", target = "product")
//    CartItemResponseDTO toDto(CartItem cartItem);
//
//    List<CartItemResponseDTO> toDtoList(List<CartItem> cartItems);
//}
