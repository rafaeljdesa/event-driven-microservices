package com.appsdeveloperblog.store.paymentsservice.query;

import com.appsdeveloperblog.store.paymentsservice.core.data.PaymentEntity;
import com.appsdeveloperblog.store.paymentsservice.core.data.PaymentRepository;
import com.appsdeveloperblog.store.core.events.PaymentProcessedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventsHandler {

    @Autowired
    private PaymentRepository paymentRepository;

    @EventHandler
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .paymentId(paymentProcessedEvent.getPaymentId())
                .orderId(paymentProcessedEvent.getOrderId())
                .build();
        paymentRepository.save(paymentEntity);
    }

}
