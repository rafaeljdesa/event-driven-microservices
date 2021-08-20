package com.appsdeveloperblog.store.ordersservice.command.rest;

import com.appsdeveloperblog.store.ordersservice.command.CreateOrderCommand;
import com.appsdeveloperblog.store.ordersservice.core.data.OrderStatus;
import org.axonframework.commandhandling.gateway.CommandGateway;
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

    @PostMapping
    public String createOrder(@RequestBody @Valid CreateOrderRestModel createOrderRestModel) {

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
            .orderId(UUID.randomUUID().toString())
            .userId(USER_ID)
            .productId(createOrderRestModel.getProductId())
            .quantity(createOrderRestModel.getQuantity())
            .addressId(createOrderRestModel.getAddressId())
            .orderStatus(OrderStatus.CREATED)
            .build();

        return commandGateway.sendAndWait(createOrderCommand);
    }

}
