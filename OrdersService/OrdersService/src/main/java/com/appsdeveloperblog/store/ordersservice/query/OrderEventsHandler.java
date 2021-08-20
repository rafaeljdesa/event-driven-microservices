package com.appsdeveloperblog.store.ordersservice.query;

import com.appsdeveloperblog.store.ordersservice.core.data.OrderEntity;
import com.appsdeveloperblog.store.ordersservice.core.data.OrderRepository;
import com.appsdeveloperblog.store.ordersservice.core.events.OrderCreatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsHandler {

    @Autowired
    private OrderRepository orderRepository;

    @EventHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(orderCreatedEvent, orderEntity);
        orderRepository.save(orderEntity);
    }

}