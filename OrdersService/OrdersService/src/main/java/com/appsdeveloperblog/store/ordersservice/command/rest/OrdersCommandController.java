package com.appsdeveloperblog.store.ordersservice.command.rest;

import com.appsdeveloperblog.store.ordersservice.command.CreateOrderCommand;
import com.appsdeveloperblog.store.ordersservice.core.data.OrderStatus;
import com.appsdeveloperblog.store.ordersservice.core.data.OrderSummay;
import com.appsdeveloperblog.store.ordersservice.query.FindOrderQuery;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {

    private static final String USER_ID = "27b95829-4f3f-4ddf-8983-151ba010e35b";

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private QueryGateway queryGateway;

    @PostMapping
    public OrderSummay createOrder(@RequestBody @Valid CreateOrderRestModel createOrderRestModel) {

        String userId = UUID.randomUUID().toString();

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
            .orderId(UUID.randomUUID().toString())
            .userId(USER_ID)
            .productId(createOrderRestModel.getProductId())
            .quantity(createOrderRestModel.getQuantity())
            .addressId(createOrderRestModel.getAddressId())
            .orderStatus(OrderStatus.CREATED)
            .build();

        SubscriptionQueryResult<OrderSummay, OrderSummay> queryResult = queryGateway.subscriptionQuery(new FindOrderQuery(userId),
                ResponseTypes.instanceOf(OrderSummay.class), ResponseTypes.instanceOf(OrderSummay.class));

        try {
            commandGateway.sendAndWait(createOrderCommand);
            return queryResult.updates().blockFirst();
        } finally {
            queryResult.close();
        }
    }

}
