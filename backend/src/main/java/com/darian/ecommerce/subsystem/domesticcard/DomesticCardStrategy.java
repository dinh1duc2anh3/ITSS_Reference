package com.darian.ecommerce.subsystem.domesticcard;

import com.darian.ecommerce.payment.dto.PaymentResult;
import com.darian.ecommerce.payment.dto.RefundResult;
import com.darian.ecommerce.payment.PaymentStrategy;
import com.darian.ecommerce.subsystem.vnpay.VNPaySubsystemService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service("domesticCard")
@Component
public class DomesticCardStrategy implements PaymentStrategy {
    private final VNPaySubsystemService vnPayService;

    public DomesticCardStrategy(VNPaySubsystemService vnPayService) {
        this.vnPayService = vnPayService;
    }

    @Override
    public PaymentResult buildPaymenturl(Long orderId, Float amount, HttpServletRequest request) {
//        VNPayRequest request = VNPayRequest.builder()
//                .orderId(orderId)
//                .amount(amount)
//                .content(content)
//                .transactionType(TransactionType.PAYMENT)
//                .bankCode("VNBANK") // Domestic bank code for VNPay
//                .language("vn")
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