package com.darian.ecommerce.product.entity;

import com.darian.ecommerce.product.enums.ProductStatus;
import com.darian.ecommerce.order.entity.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status")
    private ProductStatus productStatus;

    @JoinColumn(name = "category_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(name = "product_price")
    private Float price;

    @Column(name = "product_value")
    private Float value;

    @Column(name = "product_barcode")
    private String barcode;

    @Column(name = "product_description")
    private String description;

    @Column(name = "product_specification")
    private String specifications;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "warehouse_entry_timestamp")
    private LocalDateTime warehouseEntryDate;

    // Thêm trường weight
    @Column(name = "product_weight")
    private Float weight;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductEditHistory> editHistory;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductImage> images;

    @PrePersist
    protected void onCreate() {
        if (this.warehouseEntryDate == null) {
            this.warehouseEntryDate = LocalDateTime.now();
        }
        if (this.productStatus == null) {
            this.productStatus = ProductStatus.ACTIVE;
        }
        if (this.barcode == null || this.barcode.isBlank()) {
            this.barcode = generateBarcode();
        }
    }

    private String generateBarcode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}