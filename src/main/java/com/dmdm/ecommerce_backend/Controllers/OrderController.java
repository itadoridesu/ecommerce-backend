package com.dmdm.ecommerce_backend.Controllers;

import com.dmdm.ecommerce_backend.Dto.Orders.OrderResponseDTO;
import com.dmdm.ecommerce_backend.Dto.Orders.UpdatedOrderStatusRequest;
import com.dmdm.ecommerce_backend.Services.Orders.OrderService;
import com.dmdm.ecommerce_backend.enums.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    // the preauthrize "user" is useless here cuz we already taking care of it in security config

    private final OrderService orderService;

    // Place a new order (User only)
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponseDTO> placeOrder(HttpServletRequest request) {
        OrderResponseDTO response = orderService.placeOrder(request);
        return ResponseEntity.ok(response); // HTTP 200 OK
    }

    @PutMapping("/admin/orderId={orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdatedOrderStatusRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request, httpRequest));
    }


    // Get all orders (Admin only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrders(
            @PageableDefault(page = 0, size = 2, sort = "orderDate", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<OrderResponseDTO> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    // Get current user's orders (User only)
    @GetMapping("/filter")
    public List<OrderResponseDTO> getUserOrdersFilteredByStatus(
            @RequestParam(required = false) OrderStatus status,
            HttpServletRequest request
    ) {
        if (status != null) {
            return orderService.getUserOrdersByStatus(status, request);
        } else {
            return orderService.getUserOrders(request);
        }
    }

    // Get single order by ID (User owns it OR Admin)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id, HttpServletRequest request) {
        OrderResponseDTO dto = orderService.getOrderById(id, request);
        return ResponseEntity.ok(dto);
    }
}
