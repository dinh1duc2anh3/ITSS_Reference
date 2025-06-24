package com.darian.ecommerce.product.businesslogic.productlist;

import com.darian.ecommerce.product.mapper.ProductMapper;
import com.darian.ecommerce.product.dto.CustomerProductDTO;
import com.darian.ecommerce.product.entity.Product;
import com.darian.ecommerce.product.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerProductListFetcher implements ProductListFetcher<CustomerProductDTO>{
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Constructor injection for ProductRepository
    public CustomerProductListFetcher(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    // Fetch product list for Customer role, returning CustomerProductDTO
    @Override
    public    List<CustomerProductDTO>  fetchProductList() {
        //find all hay là find 1 số sản phẩm tiêu biểu thôi ? giữa customer và manager khi find all thì có khác gì nhau ko ,
        // có sản phẩm nào mà manager thì thấy còn custoemr thì ko thấy ko ?
        List<Product> products = productRepository.findAll();

        // logic map ntn là ntn ?
        return products.stream()
                .map(productMapper::toCustomerDTO)
                .collect(Collectors.toList());
    }



}
