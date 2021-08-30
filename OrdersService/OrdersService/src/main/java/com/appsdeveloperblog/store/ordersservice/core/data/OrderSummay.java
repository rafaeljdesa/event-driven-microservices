package com.appsdeveloperblog.store.ordersservice.core.data;

import lombok.Value;

@Value
public class OrderSummay {
    String orderId;
    OrderStatus orderStatus;
    String message;
}
