package com.appsdeveloperblog.store.ordersservice.saga;

import org.axonframework.modelling.saga.SagaEventHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@SagaEventHandler(associationProperty = "orderId")
public @interface OrderSagaEventHandler {
}
