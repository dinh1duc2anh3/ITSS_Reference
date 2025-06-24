package com.darian.ecommerce.product.service;

import com.darian.ecommerce.product.dto.ProductReviewDTO;

import java.util.List;

public interface ProductReviewService {
    // Add a review for a product
    ProductReviewDTO addReview(ProductReviewDTO reviewDTO);

    // Get all reviews for a product
    List<ProductReviewDTO> getReviews(Long productId);
}
