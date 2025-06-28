package com.darian.ecommerce.payment;

import com.darian.ecommerce.payment.dto.PaymentConfirmDTO;
import com.darian.ecommerce.shared.exception.ErrorCode;
import com.darian.ecommerce.subsystem.vnpay.VNPayConfig;
import com.darian.ecommerce.order.exception.OrderNotFoundException;
import com.darian.ecommerce.payment.exception.InvalidPaymentMethodException;
import com.darian.ecommerce.payment.dto.PaymentResDTO;
import com.darian.ecommerce.payment.dto.PaymentResult;
import com.darian.ecommerce.payment.dto.RefundResult;
import com.darian.ecommerce.payment.enums.PaymentMethod;
import com.darian.ecommerce.subsystem.vnpay.VNPayResponseHandler;
import com.darian.ecommerce.shared.constants.ApiEndpoints;
import com.darian.ecommerce.shared.constants.LoggerMessages;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping(ApiEndpoints.PAYMENT)
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:8080",
    "http://127.0.0.1:3000",
    "http://127.0.0.1:8080"
}, allowCredentials = "true")
public class PaymentController {

    // Cohesion: Functional Cohesion
    // → Class chỉ phục vụ xử lý HTTP request liên quan đến thanh toán (pay/refund), không chứa logic nghiệp vụ.

    // SRP: Không vi phạm
    // → Lớp này chỉ đóng vai trò cầu nối giữa HTTP request và service xử lý logic thanh toán.

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final VNPayResponseHandler vnPayResponseHandler;

    // Constructor injection for PaymentServiceImpl
    public PaymentController(PaymentService paymentService, VNPayResponseHandler vnPayResponseHandler) {
        this.paymentService = paymentService;
        this.vnPayResponseHandler = vnPayResponseHandler;
    }

    // Process payment for an order
    @PostMapping(ApiEndpoints.PAYMENT_BY_ORDER)
    public void payOrder(@PathVariable Long orderId,
                         @RequestParam String paymentMethod,
                         HttpServletRequest request,
                         HttpServletResponse response) throws InvalidPaymentMethodException, OrderNotFoundException, IOException {
        // Validate payment method
        try {
            log.info("🔁 INIT payOrder: orderId={}, method={}", orderId, paymentMethod);
            PaymentMethod.valueOf(paymentMethod.toUpperCase());
            log.info(LoggerMessages.PAYMENT_INIT, orderId, paymentMethod);
            String paymentUrl = paymentService.payOrder(orderId, paymentMethod, request);
            System.out.println("🔗 Redirecting to: " + paymentUrl);

//            response.sendRedirect(result.getReturnUrl());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"url\": \"" + paymentUrl + "\"}");
        } catch (IllegalArgumentException e) {
            e.printStackTrace(); // In ra lỗi cụ thể để kiểm tra
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ERROR: " + e.getMessage());
            throw new InvalidPaymentMethodException(ErrorCode.INVALID_PAYMENT_METHOD.format(paymentMethod));
        }


    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmFromReturn(@RequestBody PaymentConfirmDTO dto) {
        log.info(dto.toString());
        paymentService.handlePayment(dto);
        return ResponseEntity.ok("Giao dịch thanh toán thành công. Đã ghi nhận.");
    }


    @GetMapping("/ipn")
    public ResponseEntity<String> ipnHandler(@RequestParam String provider, HttpServletRequest req) {
        if (PaymentMethod.VNPAY.toString().equalsIgnoreCase(provider)) {
            return vnPayIpn(req);
        }
        else  {
            return codIpn(req);
        }

    }

    private ResponseEntity<String> codIpn(HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail to choose cod, haven't been developed yet.");
    }

    public ResponseEntity<String> vnPayIpn(HttpServletRequest request) {
        Map<String, String> vnpParams = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            vnpParams.put(entry.getKey(), entry.getValue()[0]);
        }

        boolean isValid = vnPayResponseHandler.validateSignature(vnpParams); // Bạn cần xử lý xác thực hash
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // Gọi service cập nhật đơn hàng theo trạng thái
        boolean updated = paymentService.handleVnPayIpn(vnpParams);
        if (updated) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail to update order");
        }
    }


    // Process refund for an order
    @PostMapping(ApiEndpoints.PAYMENT_REFUND)
    public ResponseEntity<RefundResult> processRefund(@PathVariable Long orderId) {
        log.info(LoggerMessages.PAYMENT_INIT, orderId, "refund");
        RefundResult result = paymentService.processRefund(orderId);
        log.info(LoggerMessages.PAYMENT_COMPLETED, orderId, result.getRefundStatus());
        return ResponseEntity.ok(result);
    }


    @PostMapping("/refund")
    public ResponseEntity<?> refund(@RequestParam Long orderId,
                                    @RequestParam String transactionNo,
                                    @RequestParam String transactionDate,
                                    HttpServletRequest request) {

        Map<String, String> refundBody = buildRefundRequest(orderId, transactionNo, transactionDate, request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(refundBody, headers);
        String url = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        return ResponseEntity.ok(response.getBody());
    }

    public Map<String, String> buildRefundRequest(Long orderId, String transactionNo, String transactionDate, HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString(); // Mã duy nhất
        String vnp_TxnRef = String.valueOf(orderId);
        String vnp_CreateBy = "admin"; // hoặc username thực hiện hoàn tiền
        String vnp_CreateDate = VNPayConfig.getCreateDate(); // yyyyMMddHHmmss
        String ipAddr = VNPayConfig.getIpAddress(request);
        String amount = String.valueOf(99000 * 100); // VND nhân 100

        String orderInfo = "Hoàn tiền đơn hàng " + vnp_TxnRef;

        Map<String, String> body = new HashMap<>();
        body.put("vnp_RequestId", requestId);
        body.put("vnp_Version", VNPayConfig.vnp_Version); // "2.1.0"
        body.put("vnp_Command", "refund");
        body.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        body.put("vnp_TransactionType", "02"); // toàn phần: 02
        body.put("vnp_TxnRef", vnp_TxnRef);
        body.put("vnp_Amount", amount);
        body.put("vnp_TransactionNo", transactionNo); // nếu có
        body.put("vnp_TransactionDate", transactionDate); // yyyyMMddHHmmss
        body.put("vnp_CreateBy", vnp_CreateBy);
        body.put("vnp_CreateDate", vnp_CreateDate);
        body.put("vnp_IpAddr", ipAddr);
        body.put("vnp_OrderInfo", orderInfo);

        // Tạo chuỗi data để hash
        String data = requestId + "|" + body.get("vnp_Version") + "|" + body.get("vnp_Command") + "|"
                + body.get("vnp_TmnCode") + "|" + body.get("vnp_TransactionType") + "|" + vnp_TxnRef + "|"
                + amount + "|" + transactionNo + "|" + transactionDate + "|" + vnp_CreateBy + "|"
                + vnp_CreateDate + "|" + ipAddr + "|" + orderInfo;

        String secureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, data);
        body.put("vnp_SecureHash", secureHash);

        return body;
    }
}
