package com.darian.ecommerce.order.mapper;

import com.darian.ecommerce.cart.dto.CartItemDTO;
import com.darian.ecommerce.order.dto.OrderItemDTO;
import com.darian.ecommerce.order.entity.Order;
import com.darian.ecommerce.order.entity.OrderItem;
import com.darian.ecommerce.product.entity.Product;
import com.darian.ecommerce.product.entity.ProductImage;
import com.darian.ecommerce.product.service.ProductService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderItemMapper {

    private final ProductService productService;

    public OrderItemMapper(ProductService productService) {
        this.productService = productService;
    }

    public OrderItemDTO toDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .productId(item.getProduct().getProductId())
                .productName(item.getProduct().getName())
                .productImages(item.getProduct().getImages().stream()
                        .map(ProductImage::getUrl)
                        .collect(Collectors.toList()))
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }

    public List<OrderItemDTO> toDTOList(List<OrderItem> items) {
        return items.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts an OrderItemDTO to an OrderItem entity, associating it with the given Order.
     * Fetches product details (like price and rush eligibility) from the ProductService.
     *
     * @param dto   The OrderItemDTO to convert.
     * @param order The parent Order entity to associate with the OrderItem.
     * @return The created OrderItem entity.
     */
    public OrderItem toEntity(OrderItemDTO dto, Order order) {
        Product product = productService.getProductById(dto.getProductId()); // This call likely returns Product or null
        if (product == null) {
            throw new IllegalArgumentException("Product not found for ID: " + dto.getProductId());
        }

        return OrderItem.builder()
                .product(product)
                .quantity(dto.getQuantity())
                .unitPrice(product.getPrice())
                .order(order)
                .build();
    }

    /**
     * Converts a list of OrderItemDTOs to a list of OrderItem entities,
     * associating each with the given Order.
     *
     * @param dtoList The list of OrderItemDTOs to convert.
     * @param order   The parent Order entity to associate with the OrderItems.
     * @return A list of created OrderItem entities.
     */
    public List<OrderItem> toEntityList(List<OrderItemDTO> dtoList, Order order) {
        return dtoList.stream()
                .map(dto -> this.toEntity(dto, order))
                .collect(Collectors.toList());
    }

    /**
     * Converts a CartItemDTO to an OrderItem entity, associating it with the given Order.
     * Fetches product details (like price and rush eligibility) from the ProductService.
     * This is useful when initially creating an order from a cart.
     *
     * @param cartItemDTO The CartItemDTO to convert.
     * @param order       The parent Order entity to associate with the OrderItem.
     * @return The created OrderItem entity.
     */
    public OrderItem fromCartItemDTOToEntity(CartItemDTO cartItemDTO, Order order) {
        Product product = productService.getProductById(cartItemDTO.getProductId()); // This call likely returns Product or null
        if (product == null) {
            throw new IllegalArgumentException("Product not found for ID: " + cartItemDTO.getProductId());
        }

        return OrderItem.builder()
                .product(product)
                .quantity(cartItemDTO.getQuantity())
                .unitPrice(product.getPrice())
                .order(order)
                .build();
    }

    /**
     * Converts a list of CartItemDTOs to a list of OrderItem entities,
     * associating each with the given Order.
     *
     * @param cartItemDTOs The list of CartItemDTOs to convert.
     * @param order        The parent Order entity to associate with the OrderItems.
     * @return A list of created OrderItem entities.
     */
    public List<OrderItem> fromCartItemDTOListToEntityList(List<CartItemDTO> cartItemDTOs, Order order) {
        return cartItemDTOs.stream()
                .map(cartItemDTO -> this.fromCartItemDTOToEntity(cartItemDTO, order))
                .collect(Collectors.toList());
    }
}