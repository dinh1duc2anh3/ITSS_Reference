package com.darian.ecommerce.product.businesslogic.productdetail;

import com.darian.ecommerce.product.dto.ProductDTO;

public interface ProductDetailFetcher<T extends ProductDTO> {
    // Fetch product details by product ID, returning ProductDTO or its subtypes
     T fetchProductDetails(Long productId);
}
