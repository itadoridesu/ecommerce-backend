package com.dmdm.ecommerce_backend.Mapper;

import com.dmdm.ecommerce_backend.Dto.Orders.OrderItemResponseDTO;
import com.dmdm.ecommerce_backend.Dto.Orders.OrderResponseDTO;
import com.dmdm.ecommerce_backend.Entity.Orders.Order;
import com.dmdm.ecommerce_backend.Entity.Orders.OrderItem;

import java.util.List;

import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final ProductMapper productMapper;

    public OrderMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public OrderResponseDTO toDto(Order order) {
        if (order == null) return null;

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderItems(toOrderItemDtoList(order.getOrderItems()));

        return dto;
    }

    public List<OrderResponseDTO> toDtoList(List<Order> orders) {
        if (orders == null) return null;
        return orders.stream().map(this::toDto).collect(Collectors.toList());
    }

    public OrderItemResponseDTO toOrderItemDto(OrderItem item) {
        if (item == null) return null;

        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setPriceAtPurchase(item.getPriceAtPurchase());
        dto.setProduct(productMapper.toDTO(item.getProduct()));

        return dto;
    }

    public List<OrderItemResponseDTO> toOrderItemDtoList(List<OrderItem> items) {
        if (items == null) return null;
        return items.stream().map(this::toOrderItemDto).collect(Collectors.toList());
    }
}


// instead of specifying the sources and targets manually we use uses to just put the objects
// kouna handirou target w source for all fields tae nested objects ida mdrnach uses
//@Mapper(
//        componentModel = "spring",
//        uses = {ProductMapper.class}, // Reuse existing ProductMapper
//        unmappedTargetPolicy = ReportingPolicy.IGNORE
//)
//public interface OrderMapper {
//
//    // Maps Order -> OrderResponseDTO (with nested items)
//    @Mapping(source = "orderItems", target = "orderItems")
//    OrderResponseDTO toDto(Order order);
//
//    List<OrderResponseDTO> toDtoList(List<Order> order);
//
//
//    // Maps List<OrderItem> -> List<OrderItemResponseDTO>
//    List<OrderItemResponseDTO> toOrderItemDtoList(List<OrderItem> items);
//
//    // Maps OrderItem -> OrderItemResponseDTO
//    @Mapping(source = "product", target = "product")
//    OrderItemResponseDTO toOrderItemDto(OrderItem item);
//}
