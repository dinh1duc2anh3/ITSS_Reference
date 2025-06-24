package com.darian.ecommerce.product.entity;

import com.darian.ecommerce.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_review")
public class ProductReview {
    // Primary key
    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID of the product being reviewed
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Rating given by the customer (e.g., 1-5)
    private Integer rating;

    // Comment or review content
    @Column(name = "review_comment")
    private String comment;

    // Date and time the review was created
    @Column(name = "review_timestamp")

    private LocalDateTime createdDate;
    // Auto-set createdDate
    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }
}
