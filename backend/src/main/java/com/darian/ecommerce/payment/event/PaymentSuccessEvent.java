package com.darian.ecommerce.payment.event;

import com.darian.ecommerce.payment.dto.PaymentResult;
import org.springframework.context.ApplicationEvent;
import lombok.Getter;

@Getter
public class PaymentSuccessEvent extends ApplicationEvent {
    private final PaymentResult paymentResult;

    public PaymentSuccessEvent(Object source, PaymentResult paymentResult) {
        super(source);
        this.paymentResult = paymentResult;
    }
}