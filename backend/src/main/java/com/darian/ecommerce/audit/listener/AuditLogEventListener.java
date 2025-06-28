package com.darian.ecommerce.audit.listener;

import com.darian.ecommerce.audit.event.CheckDeleteLimitEvent;
import com.darian.ecommerce.payment.dto.PaymentResult;
import com.darian.ecommerce.payment.dto.RefundResult;
import com.darian.ecommerce.payment.event.PaymentSuccessEvent;
import com.darian.ecommerce.payment.event.RefundEvent;
import com.darian.ecommerce.product.event.*;
import com.darian.ecommerce.audit.AuditLogService;
import com.darian.ecommerce.shared.constants.LoggerMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AuditLogEventListener {
    private final Logger logger = LoggerFactory.getLogger(AuditLogEventListener.class);

    private final AuditLogService auditLogService;

    public AuditLogEventListener(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @EventListener
    public void handleViewProduct(ViewProductEvent event) {
        logger.info(LoggerMessages.AUDIT_PRODUCT_ACTION, event.getUserId(), event.getRole(), "view", event.getProductId());
        auditLogService.logViewProduct(event.getProductId(), event.getUserId(), event.getRole());
    }

    @EventListener
    public void handleSearchProduct(SearchProductEvent event) {
        logger.info(LoggerMessages.AUDIT_PRODUCT_ACTION, event.getUserId(), event.getRole(), "search", event.getKeyword());
        auditLogService.logSearchAction(event.getUserId(), event.getKeyword(), event.getRole());
    }

    @EventListener
    public void handleAddProduct(AddProductEvent event) {
        logger.info(LoggerMessages.AUDIT_PRODUCT_ACTION, event.getUserId(), event.getRole(), "add", event.getProductId());
        auditLogService.logAddAction(event.getUserId(), event.getProductId(), event.getRole());
    }

    @EventListener
    public void handleUpdateProduct(UpdateProductEvent event) {
        logger.info(LoggerMessages.AUDIT_PRODUCT_ACTION, event.getUserId(), event.getRole(), "update", event.getProductId());
        auditLogService.logUpdateAction(event.getUserId(), event.getProductId(), event.getRole());
    }

    @EventListener
    public void handleDeleteProduct(DeleteProductEvent event) {
        logger.info(LoggerMessages.AUDIT_PRODUCT_ACTION, event.getUserId(), event.getRole(), "delete", event.getProductId());
        auditLogService.logDeleteAction(event.getUserId(), event.getProductId(), event.getRole());
    }

    @EventListener
    public void handleCheckDeleteLimit(CheckDeleteLimitEvent event) {
        logger.info(LoggerMessages.PRODUCT_DELETE_LIMIT, event.getUserId());
    }

    @EventListener
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        PaymentResult result = event.getPaymentResult();
        logger.info(LoggerMessages.PAYMENT_COMPLETED, result.getOrderId(), result.getPaymentStatus());
        auditLogService.logPayment(result);
    }

    @EventListener
    public void handleRefund(RefundEvent event) {
        RefundResult result = event.getRefundResult();
        logger.info("Observer - Ghi nhận hoàn tiền: {}", result.getTransactionId());
        auditLogService.logPayment(result);  // hoặc logRefund nếu tách riêng
    }
}
