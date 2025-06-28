package com.darian.ecommerce.order.dto;

import lombok.*;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private List<String> productImages; 
    private Integer quantity;
    private Float unitPrice;
    private Float lineTotal;
    private Float lineWeight;
    private boolean isRushEligible; 
}
