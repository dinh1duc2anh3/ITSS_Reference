package com.darian.ecommerce.product.mapper;

import com.darian.ecommerce.product.dto.CategoryDTO;
import com.darian.ecommerce.order.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public  CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    public  Category toEntity(CategoryDTO dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

}
