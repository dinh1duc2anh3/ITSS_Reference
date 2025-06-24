package com.darian.ecommerce.product.businesslogic.productsearch;

import com.darian.ecommerce.product.mapper.ProductMapper;
import com.darian.ecommerce.product.dto.CustomerProductDTO;
import com.darian.ecommerce.product.entity.Product;
import com.darian.ecommerce.product.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerProductSearchFetcher implements ProductSearchFetcher<CustomerProductDTO>{
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Constructor injection for ProductRepository
    public CustomerProductSearchFetcher(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    // Search products by keyword for Customer role, returning CustomerProductDTO
    @Override
    public List<CustomerProductDTO> searchProducts(String keyword) {
        // customer search thi phai log lai
        List<Product> products = productRepository.findByKeyword(keyword);
        return products.stream()
                .map(productMapper::toCustomerDTO)
                .collect(Collectors.toList());
    }

    }
