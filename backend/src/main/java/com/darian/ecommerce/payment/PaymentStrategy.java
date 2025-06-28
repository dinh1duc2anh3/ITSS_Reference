package com.darian.ecommerce.payment;

import com.darian.ecommerce.payment.dto.PaymentResult;
import com.darian.ecommerce.payment.dto.RefundResult;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;

public interface PaymentStrategy {
    // Cohesion: Functional Cohesion
    // → Interface chỉ cung cấp các hành vi xử lý giao dịch thanh toán và hoàn tiền.

    // SRP: Không vi phạm
    // → Chỉ định nghĩa hợp đồng cho Payment Subsystem, không kiêm nhiệm thêm trách nhiệm nào khác.

    PaymentResult buildPaymenturl(Long orderId, Float amount, HttpServletRequest request) throws UnsupportedEncodingException;
    RefundResult processRefund(Long orderId);
} 