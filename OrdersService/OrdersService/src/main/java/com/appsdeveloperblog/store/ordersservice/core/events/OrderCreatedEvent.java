package com.appsdeveloperblog.store.ordersservice.core.events;

import com.appsdeveloperblog.store.ordersservice.core.data.OrderStatus;
import lombok.Data;

@Data
public class OrderCreatedEvent {

    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;

}
