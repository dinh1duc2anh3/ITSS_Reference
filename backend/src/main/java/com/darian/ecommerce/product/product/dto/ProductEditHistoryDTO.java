package com.darian.ecommerce.product.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductEditHistoryDTO {
    private Long productId;
    private String productName;

    private String editor;            // Tên người chỉnh sửa
    private String editorRole;        // Vai trò (ADMIN, MANAGER, STAFF,...)

    private LocalDateTime editDate;

    private String editType;          // Loại thay đổi (PRICE_UPDATE, STOCK_CHANGE,...)
    private String summary;           // Mô tả ngắn gọn nội dung thay đổi

    private String changes;           // Nội dung raw của thay đổi (JSON hoặc diff text)

    private String previousValue;     // Giá trị cũ (tuỳ chọn nếu muốn hiển thị đối chiếu)

    private String productStatus;     // Trạng thái sản phẩm lúc thay đổi (ACTIVE, INACTIVE,...)

    private Boolean importantChange;  // Đánh dấu đây có phải là thay đổi lớn không (ví dụ: thay đổi giá)
}
