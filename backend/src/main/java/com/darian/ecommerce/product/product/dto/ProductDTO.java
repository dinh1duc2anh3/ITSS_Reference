package com.darian.ecommerce.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    private String category;
    private String name;
    private String description;
    private Float price;
    private String specifications;
    private Float weight;
    private List<String> images;  // Lưu danh sách URL thay vì kiểu String đơn lẻ



}
