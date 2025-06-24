package com.darian.ecommerce.product.businesslogic.productdetail;

import com.darian.ecommerce.product.mapper.ProductMapper;
import com.darian.ecommerce.product.dto.ProductDTO;
import com.darian.ecommerce.auth.enums.UserRole;
import com.darian.ecommerce.product.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductDetailFetcherFactory {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Constructor injection for ProductRepository
    public ProductDetailFetcherFactory(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    // Get the appropriate ProductDetailFetcher based on role
    public ProductDetailFetcher<? extends ProductDTO> getFetcher(UserRole role) {
        return switch (role) {
            case CUSTOMER -> new CustomerProductDetailFetcher(productRepository, productMapper);
            case MANAGER -> new ManagerProductDetailFetcher(productRepository, productMapper);
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };
    }
}
