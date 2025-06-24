package com.darian.ecommerce.product.businesslogic.productsearch;

import com.darian.ecommerce.product.dto.ProductDTO;

import java.util.List;

public interface ProductSearchFetcher <T extends ProductDTO> {
    // Search products by keyword, returning a list of ProductDTO or its subtypes
    List<T> searchProducts(String keyword);
}
