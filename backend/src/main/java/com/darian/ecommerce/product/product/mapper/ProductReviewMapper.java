package com.darian.ecommerce.product.mapper;

import com.darian.ecommerce.product.dto.ProductReviewDTO;
import com.darian.ecommerce.product.entity.Product;
import com.darian.ecommerce.product.entity.ProductReview;
import com.darian.ecommerce.auth.entity.User;
import com.darian.ecommerce.product.service.ProductService;
import com.darian.ecommerce.auth.UserService;
import org.springframework.stereotype.Component;

@Component
public class ProductReviewMapper {
    private final ProductService productService;
    private final UserService userService;

    public ProductReviewMapper(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    public ProductReview toEntity(ProductReviewDTO reviewDTO) {
        Product product = productService.getProductById(reviewDTO.getProductId());

        User user = userService.getUserById(reviewDTO.getCustomerId());

        return ProductReview.builder()
                .product(product)
                .user(user)
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .build();
    }

    public ProductReviewDTO toDTO(ProductReview review) {
        return ProductReviewDTO.builder()
                .productId(review.getProduct().getProductId())
                .customerId(review.getUser().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdDate(review.getCreatedDate())
                .build();
    }
}
