package com.darian.ecommerce.order.entity;

import com.darian.ecommerce.auth.entity.User;
import com.darian.ecommerce.order.enums.OrderStatus;
import com.darian.ecommerce.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    // Primary key
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    // ID of the customer who placed the order
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User user;

    // Status of the order (e.g., PENDING, SHIPPED)
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "is_rush_order")
    private Boolean isRushOrder;

    @Column(name = "rush_delivery_time")
    private LocalDateTime rushDeliveryTime;
    
    // Delivery information (1-1 relationship)
    @ManyToOne
    @JoinColumn(name = "delivery_info_id", nullable = false)
    private DeliveryInfo deliveryInfo;

    // Subtotal of the order
    private Float subtotal;

    // Shipping fee for the order
    @Column(name = "shipping_fee")
    private Float shippingFee;

    // Total amount (including shipping and discount)
    private Float total;

    // Date and time the order was created
    @Column(name = "created_timestamp")
    private LocalDateTime createdDate;

    // Discount applied to the order
    private Float discount;

    // Status of the payment (e.g., PAID, REFUNDED , UNPAID , ... )
    // Lưu tên enum như chuỗi (thay vì index)
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    // List of order items (1-* relationship)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    // Calculate subtotal (sum of line totals)
    public float getSubtotal(){
        return items != null ? (float) items.stream().mapToDouble(OrderItem::getLineTotal).sum() : 0;
    }

    // Calculate VAT 
    public Float getVAT(){
        return getSubtotal() * 0.1f;
    }

    public Float getTotal(){
        return getSubtotal() + shippingFee - discount + getVAT();
    }

    // Auto-set createdDate
    @PrePersist
    protected void onCreate() {
        this.isRushOrder = false;
    }
}
