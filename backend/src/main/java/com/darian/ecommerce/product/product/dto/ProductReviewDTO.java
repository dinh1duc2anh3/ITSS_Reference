package com.darian.ecommerce.product.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewDTO {
    private Long productId;
    private Integer customerId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdDate;

}
