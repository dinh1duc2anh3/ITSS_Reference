package com.darian.ecommerce.payment.factory;

import com.darian.ecommerce.payment.enums.PaymentMethod;
import com.darian.ecommerce.subsystem.creditcard.CreditCardStrategy;
import com.darian.ecommerce.subsystem.domesticcard.DomesticCardStrategy;
import com.darian.ecommerce.payment.PaymentStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class PaymentStrategyFactory {

    private final Map<PaymentMethod, PaymentStrategy> strategyMap = new EnumMap<>(PaymentMethod.class);

    public PaymentStrategyFactory(
            @Qualifier("vnpay") PaymentStrategy vnpay,
            @Qualifier("creditCard") PaymentStrategy creditCard,
            @Qualifier("domesticCard") PaymentStrategy domesticCard
    ) {
        strategyMap.put(PaymentMethod.VNPAY, vnpay);
        strategyMap.put(PaymentMethod.CREDIT_CARD, creditCard);
        strategyMap.put(PaymentMethod.DOMESTIC_CARD, domesticCard);
    }

    public PaymentStrategy createPaymentStrategy(PaymentMethod paymentMethod) {
        PaymentStrategy strategy = strategyMap.get(paymentMethod);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }
        return strategy;
    }
} 