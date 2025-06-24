package com.darian.ecommerce.product.service;

import com.darian.ecommerce.product.dto.RelatedProductDTO;
import com.darian.ecommerce.product.entity.Product;

import java.util.List;

public interface RelatedProductService {
    // Suggest related products based on a product ID
    List<RelatedProductDTO> suggestRelatedProducts(Long productId);

    // get related products based on a product ID
    List<Product> getRelatedProducts(Long productId);
}
