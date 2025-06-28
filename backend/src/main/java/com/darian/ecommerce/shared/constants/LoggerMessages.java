package com.darian.ecommerce.shared.constants;

public class LoggerMessages {
    // Payment related messages
    public static final String PAYMENT_INIT = "Initiating payment for order {} with method {}";
    public static final String PAYMENT_COMPLETED = "Payment completed for order {}: status {}";
    public static final String PAYMENT_FAILED = "Payment failed for order {}: {}";
    public static final String PAYMENT_VALIDATION_FAILED = "Payment validation failed for order {}";
    public static final String PAYMENT_PROCESSING = "Processing payment for order {} with method {}";


    // VNPay messages
    public static final String VNPAY_IPN_PROCESSING =  "Processing VNPay ipn for txn ref {}";
    public static final String VNPAY_IPN_COMPLETED = "Completed VNPay ipn for txn ref {}, payment status {}";
    
    // Refund related messages
    public static final String REFUND_INIT = "Initiating refund for order {} with method {}";
    public static final String REFUND_COMPLETED = "Refund completed for order {}: status {}";
    public static final String REFUND_FAILED = "Refund failed for order {}: {}";
    public static final String REFUND_VALIDATION_FAILED = "Refund validation failed for order {}";
    public static final String REFUND_PROCESSING = "Processing Refund for order {} with method {}";

    // VNPay specific messages
    public static final String VNPAY_SENDING_REQUEST = "Sending payment request to VNPay for order {}";
    public static final String VNPAY_CONNECTION_ERROR = "Failed to connect to VNPay API: {}";
    public static final String VNPAY_PAYMENT_EXECUTED = "Payment executed for order {}: status {}";
    public static final String VNPAY_REFUND_EXECUTED = "Refund executed for order {}: status {}";
    
    // Order related messages
    public static final String ORDER_CREATED = "Order created: {}";
    public static final String ORDER_PLACED = "Order placed: {}";
    public static final String ORDER_CANCELLED = "Order cancelled: {}";
    public static final String ORDER_CREATED_DRAFT = "Draft order created: {}";
    public static final String ORDER_DELETED_DRAFT= "Draft order deleted: {}";
    public static final String ORDER_UPDATED = "Order updated: {}";
    public static final String ORDER_NOT_FOUND = "Order not found: {}";
    public static final String ORDER_STATUS_CHANGED = "Order status changed from {} to {} for order {}";
    
    // Cart related messages
    public static final String CART_ADD_PRODUCT = "Adding product {} with quantity {} to cart for user {}";
    public static final String CART_UPDATE_QUANTITY = "Updating quantity of product {} to {} in cart for user {}";
    public static final String CART_REMOVE_PRODUCT = "Removing product {} from cart for user {}";
    public static final String CART_CLEARING = "Clearing cart for user {}";
    public static final String CART_CLEARED = "Cleared cart for user {}";
    public static final String CART_VIEW = "Viewing cart for user {}";
    public static final String CART_PRODUCT_NOT_AVAILABLE = "Product {} is not available for user {}";
    
    // Product related messages
    public static final String PRODUCT_VIEW = "User {} (role: {}) viewed product: {}";
    public static final String PRODUCT_LIST_FETCH = "Fetching product list for role: {}";
    public static final String PRODUCT_DETAILS_FETCH = "Fetching product details for productId: {}, role: {}, userId: {}";
    public static final String PRODUCT_SEARCH = "User {} searching products with keyword: {}, role: {}, userId: {}";
    public static final String PRODUCT_FILTER_SEARCH = "User {} finding products with filters - keyword: {}, minPrice: {}, maxPrice: {}, category: {}";
    public static final String PRODUCT_ADD = "User {} adding new product: {}";
    public static final String PRODUCT_ADD_INVALID = "Invalid product details: {}";
    public static final String PRODUCT_ADD_SUCCESS = "Product added successfully: {} by user {}";
    public static final String PRODUCT_UPDATE = "User {} updating product: {}";
    public static final String PRODUCT_UPDATE_INVALID = "Invalid product details for update: {}";
    public static final String PRODUCT_UPDATE_SUCCESS = "Product updated successfully: {} by user {}";
    public static final String PRODUCT_DELETE = "User {} attempting to delete product: {}";
    public static final String PRODUCT_DELETE_SUCCESS = "Product {} deleted successfully by user {}";
    public static final String PRODUCT_QUANTITY_CHECK = "Checking quantity for product: {}";
    public static final String PRODUCT_DELETION_VALIDATE = "Validating deletion for product {} by user {}";
    public static final String PRODUCT_DELETION_CONSTRAINT = "Deletion constraints violated for product: {}";
    public static final String PRODUCT_DELETION_ORDERS = "Active orders affected by product deletion: {}";
    public static final String PRODUCT_DELETION_CONSTRAINTS_CHECK = "Checking deletion constraints for product: {}";
    public static final String PRODUCT_ORDERS_CHECK = "Checking if orders are affected for product: {}";
    public static final String PRODUCT_DELETE_LIMIT = "Checking delete limit for user: {}";
    public static final String PRODUCT_DELETE_COUNT = "User {} has {} deletions, limit check: {}";
    
    // Audit related messages
    public static final String AUDIT_PAYMENT = "Logging transaction: {}, type: {}";
    public static final String AUDIT_ORDER = "Logging order action: {}, type: {}";
    public static final String AUDIT_ACTION_FAILED = "Failed to log {} action for user {}: {}";
    public static final String AUDIT_PRODUCT_ACTION = "User {} (role: {}) performed {} action on product: {}";
    public static final String AUDIT_ORDER_ACTION = "User {} (role: {}) performed order action: {}";
    
    // Validation messages
    public static final String VALIDATION_STARTED = "Starting validation for {}";
    public static final String VALIDATION_COMPLETED = "Validation completed for {}: result {}";
    public static final String VALIDATION_FAILED = "Validation failed for {}: {}";
    
    // Category related messages
    public static final String CATEGORY_LIST_FETCH = "Fetching all categories";
    public static final String CATEGORY_SAVE = "Saving category: {}";
    public static final String CATEGORY_SAVED = "Category saved: {}";
    
    // Product Review related messages
    public static final String REVIEW_ADD = "User {} adding review for product: {}";
    public static final String REVIEW_ADD_SUCCESS = "Review added successfully for product: {} by user {}";
    public static final String REVIEW_GET = "Getting reviews for product: {}";
    public static final String REVIEW_GET_BY_USER = "Getting reviews by user: {}";
    public static final String REVIEW_UPDATE = "User {} updating review for product: {}";
    public static final String REVIEW_UPDATE_SUCCESS = "Review updated successfully for product: {} by user {}";
    public static final String REVIEW_DELETE = "User {} deleting review for product: {}";
    public static final String REVIEW_DELETE_SUCCESS = "Review deleted successfully for product: {} by user {}";
    
    // Related Product messages
    public static final String RELATED_PRODUCTS_SUGGEST = "Suggesting related products for product: {}";
    public static final String RELATED_PRODUCTS_GET = "Getting related products for product: {}";
    public static final String RELATED_PRODUCTS_ADD = "User {} adding related product: {} to product: {}";
    public static final String RELATED_PRODUCTS_REMOVE = "User {} removing related product: {} from product: {}";


    private LoggerMessages() {
        // Private constructor to prevent instantiation
    }
} 