package com.darian.ecommerce.shared.constants;

public class ApiEndpoints {
    // Base URLs
    public static final String BASE_API = "/api/v1";
    
    // Auth endpoints
    public static final String AUTH = BASE_API + "/auth";
    public static final String AUTH_LOGIN = "/login";
    public static final String AUTH_REGISTER = "/register";
    
    // Cart endpoints
    public static final String CART = BASE_API + "/cart";
    public static final String CART_BY_USER = "/{userId}";
    public static final String CART_ADD = "/{userId}/add";
    public static final String CART_UPDATE = "/{userId}/update";
    public static final String CART_REMOVE = "/{userId}/remove";
    public static final String CART_EMPTY = "/{userId}/empty";
    
    // Product endpoints
    public static final String PRODUCTS = BASE_API + "/products";
    public static final String PRODUCTS_CUSTOMER = "/customer";
    public static final String PRODUCTS_MANAGER = "/manager";
    public static final String PRODUCT_BY_ID = "/{productId}";
    public static final String PRODUCT_CUSTOMER_DETAILS = "/customer/{productId}";
    public static final String PRODUCT_MANAGER_DETAILS = "/manager/{productId}";
    public static final String PRODUCT_CUSTOMER_SEARCH = "/customer/search";
    public static final String PRODUCT_MANAGER_SEARCH = "/manager/search";
    public static final String PRODUCT_RELATED = "/{productId}/related";
    
    // Order endpoints
    public static final String ORDERS = BASE_API + "/order";
    public static final String ORDER_CREATE = "order/create";
    public static final String ORDER_PLACE = "order/place";
    public static final String ORDER_BY_ID = "/{orderId}";
    public static final String ORDER_PAYMENT = "/{orderId}/pay";
    public static final String ORDER_CANCEL = "/{orderId}/cancel";
    public static final String ORDER_DELIVERY = "/{orderId}/delivery";
    public static final String ORDER_RUSH_DELIVERY = "/{orderId}/rush-delivery";
    
    // Payment endpoints
    public static final String PAYMENT = BASE_API + "/payment";
    public static final String PAYMENT_BY_ORDER = "/{orderId}";
    public static final String PAYMENT_REFUND = "/{orderId}/refund";
    
    // VNPay specific endpoints
    public static final String VNPAY_API_BASE = "https://sandbox.vnpayment.vn/paymentv2";
    public static final String VNPAY_PAYMENT = "/create_payment_url";
    public static final String VNPAY_REFUND = "/refund";
    public static final String VNPAY_QUERY = "/query";

    //public static final String[] ORDER_RUSH_DELIVERY = null;
    
    private ApiEndpoints() {
        // Private constructor to prevent instantiation
    }
} 