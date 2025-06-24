package com.darian.ecommerce.product.repository;

import com.darian.ecommerce.product.entity.Product;
import com.darian.ecommerce.product.entity.RelatedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RelatedProductRepository extends JpaRepository<RelatedProduct, Long> {

    @Query("SELECT r.relatedProduct FROM RelatedProduct r WHERE r.product.productId = :productId")
    List<Product> findRelatedProductsByProductId(@Param("productId") Long productId);
}
