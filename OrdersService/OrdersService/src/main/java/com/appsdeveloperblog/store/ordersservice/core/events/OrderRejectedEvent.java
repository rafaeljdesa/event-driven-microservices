package com.appsdeveloperblog.store.ordersservice.core.events;

import com.appsdeveloperblog.store.ordersservice.core.data.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderRejectedEvent {

    private final String orderId;
    private final String reason;
    private final OrderStatus orderStatus = OrderStatus.REJECTED;

}
