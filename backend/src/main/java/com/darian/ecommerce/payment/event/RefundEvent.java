package com.darian.ecommerce.payment.event;

import com.darian.ecommerce.payment.dto.RefundResult;
import org.springframework.context.ApplicationEvent;
import lombok.Getter;

@Getter
public class RefundEvent extends ApplicationEvent {
    private final RefundResult refundResult;

    public RefundEvent(Object source, RefundResult refundResult) {
        super(source);
        this.refundResult = refundResult;
    }
}