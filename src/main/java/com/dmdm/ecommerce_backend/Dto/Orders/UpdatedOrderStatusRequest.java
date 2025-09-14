package com.dmdm.ecommerce_backend.Dto.Orders;

import com.dmdm.ecommerce_backend.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatedOrderStatusRequest {
    private OrderStatus status;
}
