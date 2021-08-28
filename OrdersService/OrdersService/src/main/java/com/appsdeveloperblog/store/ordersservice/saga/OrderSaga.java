package com.appsdeveloperblog.store.ordersservice.saga;

import com.appsdeveloperblog.store.core.command.CancelProductReservationCommand;
import com.appsdeveloperblog.store.core.command.ProcessPaymentCommand;
import com.appsdeveloperblog.store.core.command.ReserveProductCommand;
import com.appsdeveloperblog.store.core.events.PaymentProcessedEvent;
import com.appsdeveloperblog.store.core.events.ProductReservationCancelledEvent;
import com.appsdeveloperblog.store.core.events.ProductReservedEvent;
import com.appsdeveloperblog.store.core.model.User;
import com.appsdeveloperblog.store.core.query.FetchUserPaymentDetailsQuery;
import com.appsdeveloperblog.store.ordersservice.command.ApproveOrderCommand;
import com.appsdeveloperblog.store.ordersservice.command.RejectOrderCommand;
import com.appsdeveloperblog.store.ordersservice.core.events.OrderApprovedEvent;
import com.appsdeveloperblog.store.ordersservice.core.events.OrderCreatedEvent;
import com.appsdeveloperblog.store.ordersservice.core.events.OrderRejectedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Saga
public class OrderSaga {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);
    private static final String PAYMENT_PROCESSING_DEADLINE = "payment-processing-deadline";
    private String scheduleId = null;

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    @StartSaga
    @OrderSagaEventHandler
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
            .orderId(orderCreatedEvent.getOrderId())
            .productId(orderCreatedEvent.getProductId())
            .quantity(orderCreatedEvent.getQuantity())
            .userId(orderCreatedEvent.getUserId())
            .build();

        LOGGER.info("OrderCreatedEvent handled for orderId: " + reserveProductCommand.getOrderId() +
                " and productId: " + reserveProductCommand.getProductId());

        commandGateway.send(reserveProductCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                // start compensating transaction
            }
        });
    }

    @OrderSagaEventHandler
    public void handle(ProductReservedEvent productReservedEvent) {
        // process user payment
        LOGGER.info("ProductReservedEvent is called for productId: " + productReservedEvent.getProductId() +
                " and orderId: " + productReservedEvent.getOrderId());

        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery =
                new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());

        User userPaymentDetails;
        try {
            userPaymentDetails = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            // start compensating transaction
            cancelProductReservation(productReservedEvent, e.getMessage());
            return;
        }

        if (userPaymentDetails == null) {
            // start compensating transaction
            cancelProductReservation(productReservedEvent, "Could not fetch user payment details");
            return;
        }

        LOGGER.info("Successfully fetched user payment details for user " + userPaymentDetails.getFirstName());

        scheduleId = deadlineManager.schedule(
            Duration.of(120, ChronoUnit.SECONDS),
            PAYMENT_PROCESSING_DEADLINE,
            productReservedEvent
        );

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();

        String result = null;
        try {
            //result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
            result = commandGateway.sendAndWait(processPaymentCommand);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            // start compensating transaction
            cancelProductReservation(productReservedEvent, e.getMessage());
            return;
        }

        if (result == null) {
            LOGGER.info("The ProcessPaymentCommand resulted in NULL. Initiating a compensating transaction");
            // start compensating transaction
            cancelProductReservation(productReservedEvent, "Could not process user payment with provided payment details");
        }

    }

    @OrderSagaEventHandler
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {

        cancelDeadline();

        ApproveOrderCommand approveOrderCommand =
                new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
        commandGateway.send(approveOrderCommand);
    }

    private void cancelDeadline() {
        if (scheduleId != null) {
            deadlineManager.cancelSchedule(PAYMENT_PROCESSING_DEADLINE, scheduleId);
            scheduleId = null;
        }
    }

    @EndSaga
    @OrderSagaEventHandler
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        LOGGER.info("Order is approved. Saga is complete for orderId: " + orderApprovedEvent);
        //SagaLifecycle.end();
    }

    @OrderSagaEventHandler
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
        // Create and send a RejectOrderCommand
        RejectOrderCommand rejectOrderCommand =
                new RejectOrderCommand(productReservationCancelledEvent.getOrderId(), productReservationCancelledEvent.getReason());
        commandGateway.send(rejectOrderCommand);
    }

    @EndSaga
    @OrderSagaEventHandler
    public void handle(OrderRejectedEvent orderRejectedEvent) {
        LOGGER.info("Successfully rejected order with id " + orderRejectedEvent.getOrderId());
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
        cancelDeadline();

        CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand.builder()
            .orderId(productReservedEvent.getOrderId())
            .productId(productReservedEvent.getProductId())
            .quantity(productReservedEvent.getQuantity())
            .userId(productReservedEvent.getUserId())
            .reason(reason)
            .build();
        commandGateway.send(cancelProductReservationCommand);
    }

    @DeadlineHandler(deadlineName = PAYMENT_PROCESSING_DEADLINE)
    public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
        LOGGER.info("Payment processing deadline took place. Sending a compensating command to cancel the product reservation");
        cancelProductReservation(productReservedEvent, "Payment timeout");
    }

}
