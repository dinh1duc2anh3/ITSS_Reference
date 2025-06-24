package com.darian.ecommerce.product.mapper;

import com.darian.ecommerce.product.entity.Product;
import com.darian.ecommerce.product.entity.ProductImage;
import com.darian.ecommerce.order.enums.AvailabilityStatus;
import com.darian.ecommerce.product.dto.*;
import com.darian.ecommerce.product.service.RelatedProductService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    private final RelatedProductService relatedProductService;
    private final ProductEditHistoryMapper productEditHistoryMapper;

    public ProductMapper(RelatedProductService relatedProductService, ProductEditHistoryMapper productEditHistoryMapper) {
        this.relatedProductService = relatedProductService;
        this.productEditHistoryMapper = productEditHistoryMapper;
    }

    public CustomerProductDTO toCustomerDTO(Product product) {
        List<String> images = product.getImages().stream()
                .map(ProductImage::getUrl)
                .collect(Collectors.toList());

        AvailabilityStatus availabilityStatus = switch (product.getStockQuantity()) {
            case 0 -> AvailabilityStatus.OUT_OF_STOCK;
            default -> product.getStockQuantity() < 50
                    ? AvailabilityStatus.LOW_STOCK
                    : AvailabilityStatus.IN_STOCK;
        };

        List<RelatedProductDTO> relatedProductDTOS = relatedProductService.suggestRelatedProducts(product.getProductId());

        return CustomerProductDTO.builder()
                .productId(product.getProductId())
                .category(product.getCategory().getName())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .specifications(product.getSpecifications())
                .images(images)
                .availabilityStatus(availabilityStatus)
                .relatedProducts(relatedProductDTOS)
                .weight(product.getWeight()) // Thêm ánh xạ weight
                .build();
    }

    public ManagerProductDTO toManagerDTO(Product product) {
        List<String> images = product.getImages().stream()
                .map(ProductImage::getUrl)
                .collect(Collectors.toList());

        List<ProductEditHistoryDTO> editHistoryDTOS = product.getEditHistory().stream()
                .map(productEditHistoryMapper::toDTO)
                .toList();

        return ManagerProductDTO.builder()
                .productId(product.getProductId())
                .category(product.getCategory().getName())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .specifications(product.getSpecifications())
                .images(images)
                .value(product.getValue())
                .barcode(product.getBarcode())
                .stockQuantity(product.getStockQuantity())
                .warehouseEntryDate(product.getWarehouseEntryDate())
                .editHistory(editHistoryDTOS)
                .weight(product.getWeight()) // Thêm ánh xạ weight
                .build();
    }

    private ProductDTO toProductDTO(Product product) {
        List<String> images = product.getImages().stream()
                .map(ProductImage::getUrl)
                .collect(Collectors.toList());

        return ProductDTO.builder()
                .productId(product.getProductId())
                .category(product.getCategory().getName())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .specifications(product.getSpecifications())
                .images(images)
                .weight(product.getWeight()) // Thêm ánh xạ weight
                .build();
    }
}