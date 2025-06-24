package com.darian.ecommerce.product.service.impl;

import com.darian.ecommerce.product.mapper.CategoryMapper;
import com.darian.ecommerce.product.dto.CategoryDTO;
import com.darian.ecommerce.order.entity.Category;
import com.darian.ecommerce.product.repository.CategoryRepository;
import com.darian.ecommerce.product.service.CategoryService;
import com.darian.ecommerce.shared.constants.LoggerMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    // Logger for logging actions and errors
    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    // Dependencies injected via constructor
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;



    // Constructor for dependency injection
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    // Get all product categories
    @Override
    public List<CategoryDTO> findAllCategories() {
        log.info(LoggerMessages.CATEGORY_LIST_FETCH);
        List<Category>  allCategory = categoryRepository.findAll();

        return allCategory.stream().map(categoryMapper::toDTO).toList();
    }

    // Save a new category
    @Override
    public CategoryDTO saveCategory(CategoryDTO category) {
        //- check again saveCategory , check if the input is category or categoryDTO
        log.info(LoggerMessages.CATEGORY_SAVE, category.getName());
        Category savedCategory = categoryRepository.save(categoryMapper.toEntity(category));
        log.info(LoggerMessages.CATEGORY_SAVED, savedCategory.getId());
        return category;
    }

}
