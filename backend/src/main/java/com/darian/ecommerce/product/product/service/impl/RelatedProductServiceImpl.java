package com.darian.ecommerce.product.service.impl;

import com.darian.ecommerce.product.mapper.RelatedProductMapper;
import com.darian.ecommerce.product.dto.RelatedProductDTO;
import com.darian.ecommerce.product.entity.Product;
import com.darian.ecommerce.product.repository.RelatedProductRepository;
import com.darian.ecommerce.product.service.RelatedProductService;
import com.darian.ecommerce.shared.constants.LoggerMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelatedProductServiceImpl implements RelatedProductService {
    // Logger for logging actions and errors
    private static final Logger logger = LoggerFactory.getLogger(RelatedProductServiceImpl.class);


    // Dependencies injected via constructor
    private final RelatedProductRepository relatedProductRepository;
    private final RelatedProductMapper relatedProductMapper;

    public RelatedProductServiceImpl(RelatedProductMapper relatedProductMapper, RelatedProductRepository relatedProductRepository, RelatedProductMapper relatedProductMapper1) {
        this.relatedProductRepository = relatedProductRepository;
        this.relatedProductMapper = relatedProductMapper1;
    }

    // Suggest related products based on product ID
    @Override
    public List<RelatedProductDTO> suggestRelatedProducts(Long productId) {
        logger.info(LoggerMessages.RELATED_PRODUCTS_SUGGEST, productId);
        List<Product> relatedProducts = relatedProductRepository.findRelatedProductsByProductId(productId);
        return relatedProducts.stream().map(relatedProductMapper::toDTO).toList();
    }

    @Override
    public List<Product> getRelatedProducts(Long productId) {
        logger.info(LoggerMessages.RELATED_PRODUCTS_GET, productId);
        List<Product> relatedProducts = relatedProductRepository.findRelatedProductsByProductId(productId);
        return relatedProducts;
    }



}
