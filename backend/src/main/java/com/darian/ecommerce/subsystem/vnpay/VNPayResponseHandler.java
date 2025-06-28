package com.darian.ecommerce.subsystem.vnpay;

import com.darian.ecommerce.order.entity.Order;
import com.darian.ecommerce.payment.dto.PaymentConfirmDTO;
import com.darian.ecommerce.payment.entity.PaymentTransaction;
import com.darian.ecommerce.payment.enums.PaymentMethod;
import com.darian.ecommerce.payment.exception.PaymentProcessingException;
import com.darian.ecommerce.payment.dto.PaymentResult;
import com.darian.ecommerce.payment.dto.RefundResult;
import com.darian.ecommerce.payment.dto.VNPayResponse;
import com.darian.ecommerce.payment.enums.PaymentStatus;
import com.darian.ecommerce.payment.enums.RefundStatus;
import com.darian.ecommerce.payment.enums.VNPayResponseStatus;
import com.darian.ecommerce.shared.constants.ErrorMessages;
import com.darian.ecommerce.shared.constants.LoggerMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class VNPayResponseHandler {
    private static final Logger log = LoggerFactory.getLogger(VNPayResponseHandler.class);
    // Cohesion: Functional Cohesion
    // → Mọi method đều xử lý kết quả trả về từ VNPay (convert response → result object).

    // SRP: Không vi phạm
    // → Class này chỉ xử lý mapping giữa `VNPayResponse` và kết quả nghiệp vụ (PaymentResult, RefundResult).


    // from vnpay response -> payment confirm dto
    public static PaymentConfirmDTO toPaymentConfirmDTO(Map<String, String> params) {
        return PaymentConfirmDTO.builder()
                .vnpTransactionNo(params.get("vnp_TransactionNo"))
                .vnpTxnRef(params.get("vnp_TxnRef"))
                .vnpAmount(params.get("vnp_Amount"))
                .vnpOrderInfo(params.get("vnp_OrderInfo"))
                .vnpPayDate(params.get("vnp_PayDate"))
                .vnpResponseCode(params.get("vnp_ResponseCode"))
                .build();
    }

    // from payment confirm dto to payment transaction
    public static PaymentTransaction toPaymentTransaction(PaymentConfirmDTO dto, Order order, PaymentMethod method) {
        float amount = Float.parseFloat(dto.getVnpAmount()) / 100f;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime payTime = LocalDateTime.parse(dto.getVnpPayDate(), formatter);

        return PaymentTransaction.builder()
                .transactionCode(dto.getVnpTransactionNo())
                .order(order)
                .totalAmount(amount)
                .transactionContent("Thanh toán đơn hàng #" + dto.getVnpTxnRef())
                .payTimestamp(payTime)
                .paymentMethod(method)
                .paymentStatus("00".equals(dto.getVnpResponseCode()) ?
                        PaymentStatus.PAID : PaymentStatus.FAILED)
                .refundStatus(RefundStatus.NOT_REQUESTED)
                .build();
    }


    // Convert VNPayResponse to RefundResult
    protected RefundResult toRefundResult(VNPayResponse response) {
        log.info(LoggerMessages.VNPAY_REFUND_EXECUTED, response.getTransactionId());
        if (response.getStatus().equals(VNPayResponseStatus.FAILURE)) {
            throw new PaymentProcessingException(String.format(ErrorMessages.VNPAY_REFUND_FAILED, response.getMessage()));
        }
        return RefundResult.builder()
                .transactionId(response.getTransactionId())
                .transactionType(response.getTransactionType())
                .refundDate(LocalDateTime.now())
                .refundStatus(RefundStatus.REFUNDED)
                .build();
    }

    public boolean validateSignature(Map<String, String> vnpParams) {
        return VNPaySubsystemService.verifyChecksum(vnpParams); // Delegate to VNPaySubsystemService for consistency
    }
}


