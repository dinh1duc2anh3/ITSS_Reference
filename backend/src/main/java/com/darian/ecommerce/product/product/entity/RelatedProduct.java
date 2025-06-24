package com.darian.ecommerce.product.entity;

import com.darian.ecommerce.product.enums.RelationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "related_product")
public class RelatedProduct {
    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relation_id")
    private Long id;

    // The main product (1-* relationship)
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // Sản phẩm gốc

    // The related product (1-* relationship)
    @ManyToOne
    @JoinColumn(name = "related_product_id")
    private Product relatedProduct; // Sản phẩm liên quan được gán

    // Type of relation (e.g., "similar", "accessory")
    @Column(name = "relation_type")
    @Enumerated(EnumType.STRING)
    private RelationType relationType; // (Tùy chọn) Loại quan hệ: "SIMILAR", "FREQUENTLY_BOUGHT_TOGETHER", v.v.
}
