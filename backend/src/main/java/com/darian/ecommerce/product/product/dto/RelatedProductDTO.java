package com.darian.ecommerce.product.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelatedProductDTO {
    private Long productId;
    private String name;
    private Float price;
    private String imageUrl;
}
