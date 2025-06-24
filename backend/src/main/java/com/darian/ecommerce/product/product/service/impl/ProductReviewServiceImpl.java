package com.darian.ecommerce.product.service.impl;

import com.darian.ecommerce.product.mapper.ProductReviewMapper;
import com.darian.ecommerce.product.dto.ProductReviewDTO;
import com.darian.ecommerce.product.entity.ProductReview;
import com.darian.ecommerce.product.repository.ProductReviewRepository;
import com.darian.ecommerce.product.service.ProductReviewService;
import com.darian.ecommerce.product.service.ProductService;
import com.darian.ecommerce.auth.UserService;
import com.darian.ecommerce.shared.constants.LoggerMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {
    // Logger for logging actions and errors
    private static final Logger logger = LoggerFactory.getLogger(ProductReviewServiceImpl.class);

    // Dependencies injected via constructor
    private final ProductReviewRepository productReviewRepository;
    private final ProductService productService;
    private final UserService userService;
    private final ProductReviewMapper productReviewMapper;


    // Constructor for dependency injection
    public ProductReviewServiceImpl(ProductReviewRepository productReviewRepository, ProductService productService, UserService userService, ProductReviewMapper productReviewMapper) {
        this.productReviewRepository = productReviewRepository;
        this.productService = productService;
        this.userService = userService;
        this.productReviewMapper = productReviewMapper;
    }

    // Add a review for a product
    @Override
    public ProductReviewDTO addReview(ProductReviewDTO reviewDTO) {
        logger.info(LoggerMessages.REVIEW_ADD, reviewDTO.getCustomerId(), reviewDTO.getProductId());
        ProductReview review = productReviewMapper.toEntity(reviewDTO);
        ProductReview savedReview = productReviewRepository.save(review);
        logger.info(LoggerMessages.REVIEW_ADD_SUCCESS, savedReview.getProduct().getProductId(), reviewDTO.getCustomerId());
        return productReviewMapper.toDTO(savedReview);
    }

    // Get all reviews for a product
    @Override
    public List<ProductReviewDTO> getReviews(Long productId) {
        logger.info(LoggerMessages.REVIEW_GET, productId);
        List<ProductReview> reviews = productReviewRepository.findByProduct_ProductId(productId);
        return reviews.stream().map(productReviewMapper::toDTO).toList();
    }

}
