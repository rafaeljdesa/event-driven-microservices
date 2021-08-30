package com.appsdeveloperblog.store.ordersservice.query;

import com.appsdeveloperblog.store.ordersservice.core.data.OrderEntity;
import com.appsdeveloperblog.store.ordersservice.core.data.OrderRepository;
import com.appsdeveloperblog.store.ordersservice.core.data.OrderSummay;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderQueriesHandler {

    @Autowired
    private OrderRepository orderRepository;

    @QueryHandler
    public OrderSummay findOrder(FindOrderQuery findOrderQuery) {
        OrderEntity orderEntity = orderRepository.findByOrderId(findOrderQuery.getOrderId());
        return new OrderSummay(orderEntity.getOrderId(), orderEntity.getOrderStatus(), "");
    }

}
