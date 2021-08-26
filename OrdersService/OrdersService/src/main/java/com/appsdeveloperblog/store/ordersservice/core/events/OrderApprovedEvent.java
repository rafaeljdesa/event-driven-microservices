package com.appsdeveloperblog.store.ordersservice.core.events;

import com.appsdeveloperblog.store.ordersservice.core.data.OrderStatus;
import lombok.Value;

@Value
public class OrderApprovedEvent {

    private final String orderId;
    private final OrderStatus orderStatus = OrderStatus.APPROVED;

}
