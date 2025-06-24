package com.darian.ecommerce.product.service.impl;

import com.darian.ecommerce.product.businesslogic.productdetail.ProductDetailFetcher;
import com.darian.ecommerce.product.businesslogic.productdetail.ProductDetailFetcherFactory;
import com.darian.ecommerce.product.businesslogic.productlist.ProductListFetcher;
import com.darian.ecommerce.product.businesslogic.productlist.ProductListFetcherFactory;
import com.darian.ecommerce.product.businesslogic.productsearch.ProductSearchFetcher;
import com.darian.ecommerce.product.businesslogic.productsearch.ProductSearchFetcherFactory;
import com.darian.ecommerce.product.dto.ManagerProductDTO;
import com.darian.ecommerce.product.dto.ProductDTO;
import com.darian.ecommerce.product.entity.Product;
import com.darian.ecommerce.auth.enums.UserRole;
import com.darian.ecommerce.product.event.AddProductEvent;
import com.darian.ecommerce.product.event.SearchProductEvent;
import com.darian.ecommerce.product.event.ViewProductEvent;
import com.darian.ecommerce.product.repository.ProductRepository;
import com.darian.ecommerce.audit.AuditLogService;
import com.darian.ecommerce.product.service.ProductService;
import com.darian.ecommerce.shared.constants.Constants;
import com.darian.ecommerce.shared.constants.ErrorMessages;
import com.darian.ecommerce.shared.constants.LoggerMessages;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    // Logger for logging actions and errors
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    // Dependencies injected via constructor
    private final ApplicationEventPublisher eventPublisher;
    private final ProductRepository productRepository;
    private final ProductListFetcherFactory productListFetcherFactory;
    private final ProductDetailFetcherFactory productDetailFetcherFactory;
    private final ProductSearchFetcherFactory productSearchFetcherFactory;
    private final AuditLogService auditLogService;

    // Constructor for dependency injection
    public ProductServiceImpl(ProductRepository productRepository,
                              ProductListFetcherFactory productListFetcherFactory,
                              ProductDetailFetcherFactory productDetailFetcherFactory,
                              ProductSearchFetcherFactory productSearchFetcherFactory,
                              AuditLogService auditLogService,
                              ApplicationEventPublisher eventPublisher
    ) {
        this.productRepository = productRepository;
        this.productListFetcherFactory = productListFetcherFactory;
        this.productDetailFetcherFactory = productDetailFetcherFactory;
        this.productSearchFetcherFactory = productSearchFetcherFactory;
        this.auditLogService = auditLogService;
        this.eventPublisher = eventPublisher;
    }

    // Fetch product list using Factory Pattern based on role
    @Override
    // Safe cast: fetcher is guaranteed to return correct subtype based on role
    @SuppressWarnings("unchecked")
    public <T extends ProductDTO> List<T> getProductList(UserRole role) {
        logger.info(LoggerMessages.PRODUCT_LIST_FETCH, role);
        ProductListFetcher<T> fetcher = (ProductListFetcher<T>) productListFetcherFactory.getFetcher(role);
        return fetcher.fetchProductList();
    }

    // Fetch product details using Factory Pattern based on role
    @Override
    // Safe cast: fetcher is guaranteed to return correct subtype based on role
    @SuppressWarnings("unchecked")
    public <T extends ProductDTO> T getProductDetails(Integer userId, Long productId, UserRole role) {
        logger.info(LoggerMessages.PRODUCT_DETAILS_FETCH, productId, role, userId);
        ProductDetailFetcher<T> fetcher = (ProductDetailFetcher<T>) productDetailFetcherFactory.getFetcher(role);
        T productDTO = fetcher.fetchProductDetails(productId);
        eventPublisher.publishEvent(new ViewProductEvent(this, productId, userId, role));
        return productDTO;
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId)));
    }

    // Search products using Factory Pattern based on role
    @Override
    // Safe cast: fetcher is guaranteed to return correct subtype based on role
    @SuppressWarnings("unchecked")
    public <T extends ProductDTO> List<T> searchProducts(Integer userId, String keyword, UserRole role) {
        logger.info(LoggerMessages.PRODUCT_SEARCH, keyword, role, userId);
        ProductSearchFetcher<T> fetcher = (ProductSearchFetcher<T>) productSearchFetcherFactory.getFetcher(role);
        List<T> results = fetcher.searchProducts(keyword);
        eventPublisher.publishEvent(new SearchProductEvent(this, userId, keyword, role));
        return results;
    }

    // Advanced search with filters, directly querying repository
    @Override
    public List<ProductDTO> findByFilters(String keyword, Float minPrice, Float maxPrice, String category) {
        logger.info(LoggerMessages.PRODUCT_FILTER_SEARCH, keyword, minPrice, maxPrice, category);
        return productRepository.findByFilters(keyword, minPrice, maxPrice, category);
    }

    // Add a new product with validation (Manager only)
    @Override
    public ManagerProductDTO addProduct(Integer userId, ProductDTO productDTO) {
        logger.info(LoggerMessages.PRODUCT_ADD, userId, productDTO.getName());
        if (!validateProductDetails(productDTO)) {
            logger.warn(LoggerMessages.PRODUCT_ADD_INVALID, productDTO);
            throw new IllegalArgumentException("Invalid product details");
        }
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setWeight(productDTO.getWeight()); // Thêm gán weight
        Product savedProduct = productRepository.save(product);
        logger.info(LoggerMessages.PRODUCT_ADD_SUCCESS, savedProduct.getProductId(), userId);
        eventPublisher.publishEvent(new AddProductEvent(this, userId, productDTO.getProductId(), UserRole.MANAGER));

        //TODO: mapto function
        ManagerProductDTO managerDTO = new ManagerProductDTO();
        managerDTO.setProductId(savedProduct.getProductId());
        managerDTO.setName(savedProduct.getName());
        managerDTO.setPrice(savedProduct.getPrice());
        managerDTO.setDescription(savedProduct.getDescription());
        managerDTO.setStockQuantity(0);
        managerDTO.setWeight(savedProduct.getWeight()); // Thêm gán weight
        return managerDTO;
    }

    // Update an existing product (Manager only)
    @Override
    public ManagerProductDTO updateProduct(Integer userId, Long productId, ProductDTO productDTO) {
        logger.info(LoggerMessages.PRODUCT_UPDATE, userId, productId);
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            logger.warn(LoggerMessages.PRODUCT_UPDATE_INVALID, productDTO);
            throw new IllegalArgumentException("Invalid product details");
        }
        if (!validateProductDetails(productDTO)) {
            logger.warn(LoggerMessages.PRODUCT_UPDATE_INVALID, productDTO);
            throw new IllegalArgumentException("Invalid product details");
        }

        //TODO: input for updating a product is only these ??? nothing else? i think not enough info here
        Product product = optionalProduct.get();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setWeight(productDTO.getWeight()); // Thêm gán weight
        Product updatedProduct = productRepository.save(product);
        logger.info(LoggerMessages.PRODUCT_UPDATE_SUCCESS, updatedProduct.getProductId(), userId);
        eventPublisher.publishEvent(new ViewProductEvent(this, productId, userId, UserRole.MANAGER));

        //TODO: return managerDTO ??? is this enough info here ?
        ManagerProductDTO managerDTO = new ManagerProductDTO();
        managerDTO.setProductId(updatedProduct.getProductId());
        managerDTO.setName(updatedProduct.getName());
        managerDTO.setPrice(updatedProduct.getPrice());
        managerDTO.setDescription(updatedProduct.getDescription());
        managerDTO.setStockQuantity(updatedProduct.getStockQuantity());
        managerDTO.setWeight(updatedProduct.getWeight()); // Thêm gán weight
        return managerDTO;
    }

    // Delete a product by ID (Manager only)
    @Override
    public void deleteProduct(Long productId, Integer userId) {
        logger.info(LoggerMessages.PRODUCT_DELETE, userId, productId);
        if (!validateDeletion(productId, userId)) return;
        //update catalog??? need more check
        //update database
        productRepository.deleteById(productId);
        logger.info(LoggerMessages.PRODUCT_DELETE_SUCCESS, productId, userId);
        eventPublisher.publishEvent(new ViewProductEvent(this, productId, userId, UserRole.MANAGER));
    }

    // Validate product details before adding/updating
    @Override
    public Boolean validateProductDetails(ProductDTO productDTO) {
        //TODO: check if enough condition here ?
        return productDTO != null &&
                productDTO.getName() != null &&
                !productDTO.getName().isBlank() &&
                productDTO.getName().length() <= Constants.MAX_PRODUCT_NAME_LENGTH &&
                productDTO.getPrice() >= Constants.MIN_PRODUCT_PRICE &&
                productDTO.getPrice() <= Constants.MAX_PRODUCT_PRICE &&
                (productDTO.getDescription() == null ||
                        productDTO.getDescription().length() <= Constants.MAX_PRODUCT_DESCRIPTION_LENGTH) &&
                (productDTO.getWeight() == null || productDTO.getWeight() >= 0); // Thêm kiểm tra weight
    }

    @Override
    public Boolean checkProductQuantity(Long productId, Integer quantity) {
        logger.info(LoggerMessages.PRODUCT_QUANTITY_CHECK, productId);
        Product product = getProductById(productId);
        return product.getStockQuantity() >= quantity;
    }

    // Validate general conditions before deleting a product
    @Override
    public Boolean validateDeletion(Long productId, Integer userId) {
        logger.info(LoggerMessages.PRODUCT_DELETION_VALIDATE, productId, userId);

        if (!checkDeletionConstraints(productId)) {
            logger.warn(LoggerMessages.PRODUCT_DELETION_CONSTRAINT, productId);
            throw new IllegalStateException("Cannot delete due to constraints");
        }

        if (checkOrdersAffected(productId)) {
            logger.warn(LoggerMessages.PRODUCT_DELETION_ORDERS, productId);
            throw new IllegalStateException("Cannot delete due to affected orders");
        }

        if (checkDeleteLimit(userId)) {
            logger.warn(LoggerMessages.PRODUCT_DELETION_ORDERS, productId);
            throw new IllegalStateException("Cannot delete due to affected orders");
        }
        return true;
    }

    // Check if the user has exceeded the deletion limit
    @Override
    public Boolean checkDeleteLimit(Integer userId) {
        logger.info(LoggerMessages.PRODUCT_DELETE_LIMIT, userId);
        Integer deleteCount = auditLogService.countDeletesByUserId(userId);
        boolean withinLimit = deleteCount < Constants.MAX_DELETE_LIMIT;
        logger.info(LoggerMessages.PRODUCT_DELETE_COUNT, userId, deleteCount, withinLimit);
        return withinLimit;
    }

    // Check constraints that might prevent deletion
    @Override
    public Boolean checkDeletionConstraints(Long productId) {
        logger.info(LoggerMessages.PRODUCT_DELETION_CONSTRAINTS_CHECK, productId);
        Product product = getProductById(productId);
        return product.getStockQuantity() > 0; // Can only delete if stock is above 0
    }

    // Check if deleting a product affects any orders
    @Override
    public Boolean checkOrdersAffected(Long productId) {
        logger.info(LoggerMessages.PRODUCT_ORDERS_CHECK, productId);
        return false; // Assume that no orders affected for now
    }
}