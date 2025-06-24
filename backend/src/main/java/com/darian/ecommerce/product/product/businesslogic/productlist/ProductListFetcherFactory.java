package com.darian.ecommerce.product.businesslogic.productlist;

import com.darian.ecommerce.product.mapper.ProductMapper;
import com.darian.ecommerce.product.dto.ProductDTO;
import com.darian.ecommerce.auth.enums.UserRole;
import com.darian.ecommerce.product.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductListFetcherFactory {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Constructor injection for ProductRepository
    public ProductListFetcherFactory(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    // Get the appropriate ProductListFetcher based on role
    public ProductListFetcher<? extends ProductDTO> getFetcher(UserRole role) {
        return switch (role){
            case CUSTOMER->  new CustomerProductListFetcher(productRepository,productMapper);
            case MANAGER -> new ManagerProductListFetcher(productRepository,productMapper);
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };
    }
}
