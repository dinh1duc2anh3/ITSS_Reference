package com.darian.ecommerce.product.service;


import com.darian.ecommerce.product.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    // Get all product categories
    List<CategoryDTO> findAllCategories();

    // Save a new category
    CategoryDTO saveCategory(CategoryDTO category);
}
