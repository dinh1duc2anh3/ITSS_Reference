package com.darian.ecommerce.product.mapper;

import com.darian.ecommerce.product.dto.ProductEditHistoryDTO;
import com.darian.ecommerce.product.entity.ProductEditHistory;
import org.springframework.stereotype.Component;

@Component
public class ProductEditHistoryMapper {
    public ProductEditHistoryDTO toDTO(ProductEditHistory history) {
        return ProductEditHistoryDTO.builder()
                .productId(history.getProduct().getProductId())
                .productName(history.getProduct().getName())
                .editor(history.getEditor() != null ? history.getEditor().getUsername() : "Unknown")
                .editorRole(history.getEditor() != null ? history.getEditor().getRole().name() : "UNKNOWN")
                .editDate(history.getEditDate())
                .editType(extractEditType(history.getChanges()))     // Optional: định nghĩa riêng
                .summary(generateSummary(history.getChanges()))     // Optional: định nghĩa riêng
                .changes(history.getChanges())
                .productStatus(history.getProduct().getProductStatus().name()) // giả sử có status
                .importantChange(checkImportantChange(history.getChanges()))
                .build();
    }

    private String extractEditType(String changes) {
        if (changes.contains("price")) return "PRICE_UPDATE";
        if (changes.contains("description")) return "DESCRIPTION_CHANGE";
        return "GENERAL_UPDATE";
    }

    private String generateSummary(String changes) {
        return changes.length() > 80 ? changes.substring(0, 77) + "..." : changes;
    }

    private boolean checkImportantChange(String changes) {
        return changes.contains("price") || changes.contains("stock");
    }

}
