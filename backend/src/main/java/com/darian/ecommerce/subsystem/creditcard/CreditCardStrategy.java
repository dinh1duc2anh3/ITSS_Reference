package com.darian.ecommerce.subsystem.creditcard;

import com.darian.ecommerce.payment.dto.PaymentResult;
import com.darian.ecommerce.payment.dto.RefundResult;
import com.darian.ecommerce.payment.PaymentStrategy;
import com.darian.ecommerce.subsystem.vnpay.VNPaySubsystemService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service("creditCard")
@Component
public class CreditCardStrategy implements PaymentStrategy {
//    private final VNPaySubsystemService vnPayService;

    public CreditCardStrategy(VNPaySubsystemService vnPayService) {
//        this.vnPayService = vnPayService;
    }

    @Override
    public PaymentResult buildPaymenturl(Long orderId, Float amount, HttpServletRequest request) {
//        VNPayRequest request = VNPayRequest.builder()
//                .orderId(orderId)
//                .amount(amount)
//                .content(content)
//                .transactionType(TransactionType.PAYMENT)
//                .bankCode("INTCARD") // International card code for VNPay
//                .build();
//
//        return vnPayService.processPayment(orderId, amount, content);
        return null;
    }

    @Override
    public RefundResult processRefund(Long orderId) {
//        return vnPayService.processRefund(orderId);
        return null;
    }
}