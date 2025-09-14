package com.dmdm.ecommerce_backend.Services.Orders;

import com.dmdm.ecommerce_backend.Config.JwtService;
import com.dmdm.ecommerce_backend.Dto.Orders.OrderResponseDTO;
import com.dmdm.ecommerce_backend.Dto.Orders.UpdatedOrderStatusRequest;
import com.dmdm.ecommerce_backend.Entity.CartItem;
import com.dmdm.ecommerce_backend.Entity.Orders.Order;
import com.dmdm.ecommerce_backend.Entity.Products.Product;
import com.dmdm.ecommerce_backend.Entity.User;
import com.dmdm.ecommerce_backend.Mapper.CartItemMapper;
import com.dmdm.ecommerce_backend.Mapper.OrderMapper;
import com.dmdm.ecommerce_backend.Repositories.CartItemRepository;
import com.dmdm.ecommerce_backend.Repositories.Orders.OrderRepository;
import com.dmdm.ecommerce_backend.Repositories.Products.ProductRepository;
import com.dmdm.ecommerce_backend.Repositories.UserRepository;
import com.dmdm.ecommerce_backend.enums.OrderStatus;
import com.dmdm.ecommerce_backend.enums.Role;
import com.dmdm.ecommerce_backend.helpers.OrderHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ProductRepository productRepository;


    private final CartItemMapper cartItemMapper;
    private final OrderMapper orderMapper;

    public OrderResponseDTO placeOrder(HttpServletRequest request) {
        User user = getCurrentUser(request);

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot place order.");
        }

        // 1. Validate stock availability
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (cartItem.getQuantity() > product.getStockQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }
            // 2. Deduct stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
        }

        // 3. Save updated product stock (optional, but explicit)
        //    This is optional because they're managed entities inside a @Transactional context.
        //    But if you'd rather be sure:
        productRepository.saveAll(
                cartItems.stream().map(CartItem::getProduct).collect(Collectors.toSet())
        );

        // Creating an order using helper method
        Order order = orderRepository.save(OrderHelper.fromCartItems(cartItems, user));

        // ✅ Clear cart after order
        cartItemRepository.deleteAll(cartItems);

        // ✅ Return OrderResponseDTO using OrderMapper
        return orderMapper.toDto(order);
    }

    public OrderResponseDTO updateOrderStatus(Long orderId, UpdatedOrderStatusRequest request, HttpServletRequest httpRequest) {
        User user = getCurrentUser(httpRequest);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(request.getStatus());
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toDto(updatedOrder);
    }

    public List<OrderResponseDTO> getUserOrdersByStatus(OrderStatus status, HttpServletRequest httpRequest) {
        User user = getCurrentUser(httpRequest);
        List<Order> ordersByStatus = orderRepository.findByUserAndStatus(user, status);

        if (ordersByStatus.isEmpty()) {
            throw new RuntimeException("No orders found with status: " + status);
        }

        return orderMapper.toDtoList(ordersByStatus);
    }

    // For Admins only
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        //equivalent to order -> orderMapper.toDto(order)
        return orders.map(orderMapper::toDto);

    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getUserOrders(HttpServletRequest request) {
        User user = getCurrentUser(request);
        List<Order> orders = orderRepository.findByUser(user);

        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }


    // Only Admin or User who is an owner of the Order
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id, HttpServletRequest request) {
        User user = getCurrentUser(request);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean isOwner = order.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole().contains(Role.ADMIN);

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Access denied");
        }

        return orderMapper.toDto(order);
    }



    @Transactional(readOnly = true)
    private User getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtService.extractUsername(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
