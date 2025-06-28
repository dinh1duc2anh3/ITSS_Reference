package com.darian.ecommerce.order.mapper;

import com.darian.ecommerce.order.dto.BaseOrderDTO;
import com.darian.ecommerce.order.dto.OrderDTO;
import com.darian.ecommerce.order.dto.RushOrderDTO;
import com.darian.ecommerce.order.entity.Order;
import com.darian.ecommerce.auth.entity.User;
import com.darian.ecommerce.auth.UserService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderMapper {
    private final OrderItemMapper orderItemMapper;
    private final DeliveryInfoMapper deliveryInfoMapper;
    private final UserService userService;

    public OrderMapper(OrderItemMapper orderItemMapper, DeliveryInfoMapper deliveryInfoMapper, UserService userService) {
        this.orderItemMapper = orderItemMapper;
        this.deliveryInfoMapper = deliveryInfoMapper;
        this.userService = userService;
    }

    public BaseOrderDTO toBaseOrderDTO(Order order) {
        return (BaseOrderDTO) BaseOrderDTO.builder()
                .orderId(order.getOrderId())
                .customerId(order.getUser().getId())
                .items(orderItemMapper.toDTOList(order.getItems()))
                .orderStatus(order.getOrderStatus())
                .deliveryInfo(deliveryInfoMapper.toDTO(order.getDeliveryInfo()))
                .subtotal(order.getSubtotal())
                .shippingFee(order.getShippingFee())
                .total(order.getTotal())
                .createdDate(LocalDateTime.now()) // or order.getCreatedDate()
                .build();
    }

    public OrderDTO toOrderDTO(Order order) {
        return (OrderDTO) OrderDTO.builder()
                .orderId(order.getOrderId())
                .customerId(order.getUser().getId())
                .items(orderItemMapper.toDTOList(order.getItems()))
                .orderStatus(order.getOrderStatus())
                .deliveryInfo(deliveryInfoMapper.toDTO(order.getDeliveryInfo()))
                .subtotal(order.getSubtotal())
                .shippingFee(order.getShippingFee())
                .total(order.getTotal())
                .createdDate(LocalDateTime.now()) // or order.getCreatedDate()
                .build();
    }

    public RushOrderDTO toRushOrderDTO(Order order) {
        return RushOrderDTO.builder()
                .orderId(order.getOrderId())
                .customerId(order.getUser().getId())
                .items(orderItemMapper.toDTOList(order.getItems()))
                .orderStatus(order.getOrderStatus())
                .deliveryInfo(deliveryInfoMapper.toDTO(order.getDeliveryInfo()))
                .subtotal(order.getSubtotal())
                .shippingFee(order.getShippingFee())
                .total(order.getTotal())
                .createdDate(LocalDateTime.now()) // or order.getCreatedDate()
                .rushDeliveryTime(order.getRushDeliveryTime())
                .build();
    }

    public Order toEntity(BaseOrderDTO orderDTO, Boolean isRush, LocalDateTime rushDeliveryTime) {
        // Lấy user từ customerId (nếu cần)
        User user = userService.getUserById(orderDTO.getCustomerId()); // Ensure this handles not-found scenarios (e.g., returns Optional and you use orElseThrow, or throws exception directly)

        // 1. Build the Order object first, without setting the items initially
        Order order = Order.builder()
                .orderId(orderDTO.getOrderId())
                .user(user)
                .orderStatus(orderDTO.getOrderStatus())
                .isRushOrder(isRush)
                .deliveryInfo(deliveryInfoMapper.toEntity(orderDTO.getDeliveryInfo()))
                .subtotal(orderDTO.getSubtotal())
                .shippingFee(orderDTO.getShippingFee())
                .total(orderDTO.getTotal())
                .createdDate(orderDTO.getCreatedDate() != null ? orderDTO.getCreatedDate() : LocalDateTime.now())
                .build();
        if (orderDTO.getItems() != null && !orderDTO.getItems().isEmpty()) {
            order.setItems(orderItemMapper.toEntityList(orderDTO.getItems(), order));
        }
        if (isRush) {
            order.setRushDeliveryTime(rushDeliveryTime);
        }
        return order;
    }
}
