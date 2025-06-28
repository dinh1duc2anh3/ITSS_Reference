package com.darian.ecommerce.subsystem.vnpay;

import com.darian.ecommerce.payment.dto.VNPayRequest;
import com.darian.ecommerce.payment.dto.VNPayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockVNPayClient {
    // Cohesion: Functional Cohesion
    // → Mọi method trong class đều phục vụ chung một mục đích là "mô phỏng giao tiếp với VNPay" (payment/refund), không bị lệch nhiệm vụ.
    
    // SRP: Không vi phạm
    // → Lớp chỉ chịu trách nhiệm mô phỏng xử lý từ phía hệ thống VNPay (mock API behavior).
    // Tuy nhiên, tên class nên được đặt rõ hơn như `MockVNPayClient` để thể hiện rõ trách nhiệm mô phỏng.
    private final VNPayConfig vnPayConfig;

    @Autowired
    public MockVNPayClient(VNPayConfig vnPayConfig) {
        this.vnPayConfig = vnPayConfig;
    }


    public VNPayResponse processRefund(VNPayRequest request) {
        // Implementation for refund process
        // This will be implemented based on VNPay's refund API specification
        return null;
    }


}
