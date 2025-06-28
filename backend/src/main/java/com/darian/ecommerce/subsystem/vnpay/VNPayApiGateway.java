package com.darian.ecommerce.subsystem.vnpay;

import com.darian.ecommerce.payment.exception.ConnectionException;
import com.darian.ecommerce.payment.dto.VNPayRequest;
import com.darian.ecommerce.payment.dto.VNPayResponse;
import com.darian.ecommerce.shared.constants.Constants;
import com.darian.ecommerce.shared.constants.ErrorMessages;
import com.darian.ecommerce.shared.constants.LoggerMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class VNPayApiGateway {
    // Cohesion: Functional Cohesion
    // → Tất cả method đều hướng tới một chức năng duy nhất là gọi API (gửi payment/refund request).

    // SRP: Không vi phạm
    // → Class chỉ chịu trách nhiệm trung gian kết nối giữa hệ thống và VNPay (abstract API calls, thêm logging, xử lý connection error).

    //TODO: dùng để gửi http request tới vnpay như refund hoặc query status


    private static final Logger log = LoggerFactory.getLogger(VNPayApiGateway.class);
    private final MockVNPayClient mockVnPayClient;
    private final VNPayConfig vnPayConfig;

    protected VNPayApiGateway(MockVNPayClient mockVnPayClient, VNPayConfig vnPayConfig) {
        this.mockVnPayClient = mockVnPayClient;
        this.vnPayConfig = vnPayConfig;
    }

    @Retryable(
        value = ConnectionException.class,
        maxAttempts = Constants.MAX_PAYMENT_RETRIES,
        backoff = @Backoff(delay = Constants.PAYMENT_RETRY_DELAY_MS)
    )
    public VNPayResponse sendPaymentRequest(VNPayRequest request) throws ConnectionException {
        try {
            log.info("Sending payment request to VNPay for order: {}", request.getOrderId());
            //TODO : fix this
//            VNPayResponse response = mockVnPayClient.createPaymentUrl(request);
            VNPayResponse response = new VNPayResponse();
            log.info(LoggerMessages.VNPAY_PAYMENT_EXECUTED, request.getOrderId(), response.getStatus());
            return response;
        } catch (Exception e) {
            log.error(LoggerMessages.VNPAY_CONNECTION_ERROR, e.getMessage());
            throw new ConnectionException(ErrorMessages.VNPAY_CONNECTION_ERROR);
        }

    }

    @Retryable(
        value = ConnectionException.class,
        maxAttempts = Constants.MAX_PAYMENT_RETRIES,
        backoff = @Backoff(delay = Constants.PAYMENT_RETRY_DELAY_MS)
    )
    public VNPayResponse sendRefundRequest(VNPayRequest request) throws ConnectionException {
        try {
            log.info(LoggerMessages.VNPAY_SENDING_REQUEST, request.getOrderId());
            VNPayResponse response = mockVnPayClient.processRefund(request);

            log.info(LoggerMessages.VNPAY_REFUND_EXECUTED,
                    request.getOrderId(), response.getStatus());
            return response;
        } catch (Exception e) {
            log.error(LoggerMessages.VNPAY_CONNECTION_ERROR, e.getMessage());
            throw new ConnectionException(ErrorMessages.VNPAY_CONNECTION_ERROR);
        }
    }

    public String createPaymentRequest(Map<String, String> vnpParams) {
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnpParams.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                try {
                    hashData.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                        query.append('&');
                        hashData.append('&');
                    }
                } catch (UnsupportedEncodingException e) {
                    log.error("Error encoding field {}: {}", fieldName, e.getMessage());
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(vnPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return vnPayConfig.vnp_PayUrl + "?" + queryUrl;
    }
}
