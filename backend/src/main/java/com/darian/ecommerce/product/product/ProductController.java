package com.darian.ecommerce.product;

import com.darian.ecommerce.product.dto.CustomerProductDTO;
import com.darian.ecommerce.product.dto.ManagerProductDTO;
import com.darian.ecommerce.product.dto.ProductDTO;
import com.darian.ecommerce.product.dto.RelatedProductDTO;
import com.darian.ecommerce.auth.enums.UserRole;
import com.darian.ecommerce.product.service.ProductService;
import com.darian.ecommerce.product.service.RelatedProductService;
import com.darian.ecommerce.shared.constants.ApiEndpoints;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.PRODUCTS)
public class ProductController {
    private final ProductService productService;
    private final RelatedProductService relatedProductService;
    private final ResourceUrlProvider resourceUrlProvider;

    public ProductController(ProductService productService, RelatedProductService relatedProductService, ResourceUrlProvider resourceUrlProvider) {
        this.productService = productService;
        this.relatedProductService = relatedProductService;
        this.resourceUrlProvider = resourceUrlProvider;
    }

    @GetMapping(ApiEndpoints.PRODUCTS_CUSTOMER)
    public ResponseEntity<List<CustomerProductDTO>> getCustomerProductList() {
        List<CustomerProductDTO> products = productService.getProductList(UserRole.CUSTOMER);
        return ResponseEntity.ok(products);
    }

    @GetMapping(ApiEndpoints.PRODUCTS_MANAGER)
    public ResponseEntity<List<ManagerProductDTO>> getManagerProductList() {
        List<ManagerProductDTO> products = productService.getProductList(UserRole.MANAGER);
        return ResponseEntity.ok(products);
    }

    @GetMapping(ApiEndpoints.PRODUCT_CUSTOMER_DETAILS)
    public ResponseEntity<CustomerProductDTO> getCustomerProductDetails(
            @RequestParam Integer userId,
            @PathVariable Long productId) {
        CustomerProductDTO dto = productService.getProductDetails(userId, productId, UserRole.CUSTOMER);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(ApiEndpoints.PRODUCT_MANAGER_DETAILS)
    public ResponseEntity<ManagerProductDTO> getManagerProductDetails(
            @RequestParam Integer userId,
            @PathVariable Long productId) {
        ManagerProductDTO dto = productService.getProductDetails(userId, productId, UserRole.MANAGER);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(ApiEndpoints.PRODUCT_CUSTOMER_SEARCH)
    public ResponseEntity<List<CustomerProductDTO>> searchProductsForCustomer(
            @RequestParam Integer userId,
            @RequestParam String keyword) {
        List<CustomerProductDTO> products = productService.searchProducts(userId, keyword, UserRole.CUSTOMER);
        return ResponseEntity.ok(products);
    }

    @GetMapping(ApiEndpoints.PRODUCT_MANAGER_SEARCH)
    public ResponseEntity<List<ManagerProductDTO>> searchProductsForManager(
            @RequestParam Integer userId,
            @RequestParam String keyword) {
        List<ManagerProductDTO> products = productService.searchProducts(userId, keyword, UserRole.MANAGER);
        return ResponseEntity.ok(products);
    }

    @GetMapping(ApiEndpoints.PRODUCT_RELATED)
    public ResponseEntity<List<RelatedProductDTO>> getRelatedProducts(@PathVariable Long productId) {
        List<RelatedProductDTO> relatedProducts = relatedProductService.suggestRelatedProducts(productId);
        return ResponseEntity.ok(relatedProducts);
    }

    @PostMapping
    public ResponseEntity<ManagerProductDTO> addProduct(
            @RequestParam Integer userId,
            @RequestBody ProductDTO productDTO) {
        ManagerProductDTO createdProduct = productService.addProduct(userId, productDTO);
        return ResponseEntity.ok(createdProduct);
    }

    @DeleteMapping(ApiEndpoints.PRODUCT_BY_ID)
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId,
            @RequestParam Integer userId) {
        productService.deleteProduct(productId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(ApiEndpoints.PRODUCT_BY_ID)
    public ResponseEntity<ManagerProductDTO> updateProduct(
            @RequestParam Integer userId,
            @PathVariable Long productId,
            @RequestBody ProductDTO productDTO) {
        ManagerProductDTO updatedProduct = productService.updateProduct(userId, productId, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }
}
