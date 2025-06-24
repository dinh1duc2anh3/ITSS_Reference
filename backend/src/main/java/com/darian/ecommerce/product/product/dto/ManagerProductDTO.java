package com.darian.ecommerce.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerProductDTO extends ProductDTO {
    private Float value;
    private String barcode;
    private Integer stockQuantity;
    private LocalDateTime warehouseEntryDate;
    private List<ProductEditHistoryDTO> editHistory;
}
