package com.darian.ecommerce.subsystem.vnpay;

import com.darian.ecommerce.payment.enums.PaymentStatus;
import com.darian.ecommerce.payment.dto.PaymentResult;
import com.darian.ecommerce.payment.dto.RefundResult;
import com.darian.ecommerce.payment.dto.VNPayRequest;
import com.darian.ecommerce.payment.dto.VNPayResponse;
import com.darian.ecommerce.payment.PaymentStrategy;
import com.darian.ecommerce.shared.constants.LoggerMessages;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service("vnpay")
public class VNPaySubsystemService implements PaymentStrategy {
    // Cohesion: Functional Cohesion
    // → Các method đều xử lý một tác vụ cụ thể là "thực thi quy trình thanh toán hoặc hoàn tiền qua VNPay", từ build request → gửi → xử lý response.

    // SRP: Không vi phạm
    // → Lớp này đảm nhiệm một use case cụ thể (payment flow). Tuy chứa nhiều thành phần, nhưng tất cả đều phục vụ một mục đích duy nhất


    private static final Logger log = LoggerFactory.getLogger(VNPaySubsystemService.class);

    private final MockVNPayClient vnPayAPI;
    private final VNPayConfig vnPayConfig;
    private final VNPayBuilder builder;
    private final VNPayResponseHandler responseHandler;
    private final VNPayApiGateway apiGateway;
    
    public VNPaySubsystemService(MockVNPayClient mockVnPayClient, VNPayConfig vnPayConfig, VNPayBuilder builder, VNPayResponseHandler responseHandler, VNPayApiGateway apiGateway) {
        this.vnPayAPI = mockVnPayClient;
        this.vnPayConfig = vnPayConfig;
        this.builder = builder;
        this.responseHandler = responseHandler;
        this.apiGateway = apiGateway;
    }


    @Override
    public PaymentResult buildPaymenturl(Long orderId, Float amount, HttpServletRequest request) throws UnsupportedEncodingException {
        log.info(LoggerMessages.PAYMENT_PROCESSING, orderId, "VNPay");
        String paymentUrl = VNPayBuilder.buildPaymentUrl(orderId, amount,request);
        PaymentResult result = new PaymentResult();
        result.setReturnUrl(paymentUrl);
        return result;
    }

    @Override
    // Execute refund via VNPay
    public RefundResult processRefund(Long orderId) {
        log.info(LoggerMessages.REFUND_PROCESSING, orderId, "VNPay");
        VNPayRequest request = builder.buildRefundRequest(orderId);
        VNPayResponse response = apiGateway.sendRefundRequest(request);
        log.info(LoggerMessages.REFUND_COMPLETED, orderId, response.getStatus());
        return responseHandler.toRefundResult(response);
    }

    public PaymentResult processVNPayReturn(Map<String, String> vnpResponse) {
        PaymentResult result = new PaymentResult();
        
        String vnp_ResponseCode = vnpResponse.get("vnp_ResponseCode");
        String vnp_TransactionStatus = vnpResponse.get("vnp_TransactionStatus");
        String vnp_TxnRef = vnpResponse.get("vnp_TxnRef");
        String vnp_Amount = vnpResponse.get("vnp_Amount");
        String vnp_OrderInfo = vnpResponse.get("vnp_OrderInfo");
        
        // Verify checksum
        if (verifyChecksum(vnpResponse)) {
            if ("00".equals(vnp_ResponseCode) && "00".equals(vnp_TransactionStatus)) {
                result.setPaymentStatus(PaymentStatus.PAID);
            } else {
                result.setPaymentStatus(PaymentStatus.FAILED);
            }
        } else {
            result.setPaymentStatus(PaymentStatus.FAILED);
            log.error("Invalid checksum from VNPay response");
        }
        
        result.setTransactionId(vnp_TxnRef);
        result.setTotalAmount(Float.parseFloat(vnp_Amount) / 100); // Convert from VND cents to VND
        result.setTransactionContent(vnp_OrderInfo);
        
        return result;
    }

    protected static boolean verifyChecksum(Map<String, String> fields) {
        String secureHash = fields.remove("vnp_SecureHash");
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                try {
                    hashData.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                        hashData.append('&');
                    }
                } catch (UnsupportedEncodingException e) {
                    log.error("Error encoding field {}: {}", fieldName, e.getMessage());
                }
            }
        }
        String calculatedHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        return secureHash != null && secureHash.equals(calculatedHash);
    }



//
//    // Execute payment via VNPay\
//    TODO: for ipn url flow only
//    public PaymentResult processPayment(Long orderId, Float amount) {
//        log.info(LoggerMessages.PAYMENT_PROCESSING, orderId, "VNPay");
//        VNPayRequest request = builder.buildPaymentRequest(orderId, amount, content);
//        VNPayResponse response = apiGateway.sendPaymentRequest(request);
//        PaymentResult result = responseHandler.toPaymentResult(response);
//        result.setTotalAmount(amount);
//        result.setTransactionContent(content);
//
//        log.info(LoggerMessages.PAYMENT_COMPLETED, orderId, result.getPaymentStatus());
//        return result;
//    }
}
