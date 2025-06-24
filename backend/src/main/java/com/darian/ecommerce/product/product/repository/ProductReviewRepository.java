package com.darian.ecommerce.product.repository;

import com.darian.ecommerce.product.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview,Long> {
    // Save a product review (inherited from JpaRepository)
    ProductReview save(ProductReview review);

    // Find reviews by product ID
    List<ProductReview> findByProduct_ProductId(Long productId);
}
