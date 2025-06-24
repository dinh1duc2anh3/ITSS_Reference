package com.darian.ecommerce.product.enums;

public enum ProductStatus {
    ACTIVE,              // Sản phẩm đang hoạt động và hiển thị để bán
    INACTIVE,            // Sản phẩm bị tắt hiển thị, tạm ngừng bán
    OUT_OF_STOCK,        // Sản phẩm đã hết hàng trong kho
    DISCONTINUED,        // Sản phẩm đã ngừng kinh doanh vĩnh viễn
    DRAFT,               // Sản phẩm đang ở trạng thái nháp, chưa public
    PENDING_APPROVAL,    // Sản phẩm đang chờ kiểm duyệt trước khi hiển thị
    DELETED,             // Sản phẩm đã bị xóa mềm (soft delete)
    ARCHIVED             // Sản phẩm đã được lưu trữ, không còn sử dụng
}
