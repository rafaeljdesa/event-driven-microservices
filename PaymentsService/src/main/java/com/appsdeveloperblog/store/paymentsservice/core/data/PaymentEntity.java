package com.appsdeveloperblog.store.paymentsservice.core.data;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    private String paymentId;

    @Column
    public String orderId;

}
