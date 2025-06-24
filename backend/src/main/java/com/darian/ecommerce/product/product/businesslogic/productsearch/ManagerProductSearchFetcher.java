package com.darian.ecommerce.product.businesslogic.productsearch;

import com.darian.ecommerce.product.mapper.ProductMapper;
import com.darian.ecommerce.product.dto.ManagerProductDTO;
import com.darian.ecommerce.product.entity.Product;
import com.darian.ecommerce.product.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

public class ManagerProductSearchFetcher implements ProductSearchFetcher<ManagerProductDTO>{
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Constructor injection for ProductRepository
    public ManagerProductSearchFetcher(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    // Search products by keyword for Manager role, returning ManagerProductDTO
    @Override
    public  List<ManagerProductDTO> searchProducts(String keyword) {
        List<Product> products = productRepository.findByKeyword(keyword);
        return products.stream()
                .map(productMapper::toManagerDTO)
                .collect(Collectors.toList());
    }
}
