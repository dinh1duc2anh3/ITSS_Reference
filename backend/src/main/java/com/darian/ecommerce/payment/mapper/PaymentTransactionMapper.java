package com.darian.ecommerce.payment.mapper;

import com.darian.ecommerce.order.entity.Order;
import com.darian.ecommerce.payment.dto.PaymentConfirmDTO;
import com.darian.ecommerce.payment.dto.PaymentResult;
import com.darian.ecommerce.payment.entity.PaymentTransaction;
import com.darian.ecommerce.payment.enums.PaymentMethod;
import com.darian.ecommerce.payment.enums.PaymentStatus;
import com.darian.ecommerce.payment.enums.RefundStatus;
import com.darian.ecommerce.payment.enums.TransactionType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PaymentTransactionMapper {


    public static PaymentResult toPaymentResult(PaymentTransaction transaction) {
        return PaymentResult.builder()
                .transactionType(TransactionType.PAYMENT) // Giả định luôn là thanh toán, có thể truyền thêm nếu cần
                .orderId(transaction.getOrder().getOrderId())
                .transactionId(transaction.getTransactionCode())
                .transactionDate(transaction.getPayTimestamp())
                .totalAmount(transaction.getTotalAmount())
                .transactionContent(transaction.getTransactionContent())
                .paymentStatus(transaction.getPaymentStatus())
                .build();
    }
}