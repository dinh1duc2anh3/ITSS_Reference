package com.darian.ecommerce.order;

import com.darian.ecommerce.cart.dto.CartDTO;
import com.darian.ecommerce.order.dto.*;
import com.darian.ecommerce.order.enums.OrderStatus;
import com.darian.ecommerce.order.exception.OrderNotFoundException;
import com.darian.ecommerce.shared.constants.ApiEndpoints;
import com.darian.ecommerce.shared.constants.LoggerMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.ORDERS)
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(ApiEndpoints.ORDER_CREATE)
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CartDTO cartDTO) {
        OrderDTO result = orderService.createOrder(cartDTO);
        log.info(LoggerMessages.ORDER_CREATED, result.getOrderId());
        return ResponseEntity.ok(result);
    }

    @PostMapping(ApiEndpoints.ORDER_PLACE)
    public ResponseEntity<SplitOrderDTO> placeOrder(@RequestBody OrderDTO orderDTO) {
        SplitOrderDTO result = orderService.placeOrder(orderDTO);
        log.info(LoggerMessages.ORDER_CREATED, result);
        return ResponseEntity.ok(result);
        
    }

    @PutMapping(ApiEndpoints.ORDER_CANCEL)
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) throws OrderNotFoundException {
        log.info(LoggerMessages.ORDER_STATUS_CHANGED, "current", "CANCELLED", orderId);
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(ApiEndpoints.ORDER_BY_ID)
    public ResponseEntity<BaseOrderDTO> getOrderDetails(@PathVariable Long orderId) throws OrderNotFoundException {
        BaseOrderDTO result = orderService.getOrderDetails(orderId);
        log.info(LoggerMessages.ORDER_UPDATED, orderId);
        return ResponseEntity.ok(result);
    }

    @PutMapping(ApiEndpoints.ORDER_RUSH_DELIVERY)
    public ResponseEntity<RushOrderDTO> setRushDeliveryInfo(@PathVariable Long orderId,
                                                            @RequestBody RushOrderDeliveryInfoDTO rushOrderDeliveryInfoDTO) throws OrderNotFoundException {
        RushOrderDTO result = orderService.setRushDeliveryInfo(orderId, rushOrderDeliveryInfoDTO);
        log.info(LoggerMessages.ORDER_UPDATED, orderId);
        return ResponseEntity.ok(result);
    }


    @PutMapping(ApiEndpoints.ORDER_DELIVERY)
    public ResponseEntity<OrderDTO> setDeliveryInfo(@PathVariable Long orderId,
                                                    @RequestBody DeliveryInfoDTO deliveryInfoDTO) throws OrderNotFoundException {
        OrderDTO result = orderService.setDeliveryInfo(orderId, deliveryInfoDTO);
        log.info(LoggerMessages.ORDER_UPDATED, orderId);
        return ResponseEntity.ok(result);
    }

    @GetMapping(ApiEndpoints.ORDER_BY_ID + "/invoice")
    public ResponseEntity<InvoiceDTO> getInvoice(@PathVariable Long orderId) throws OrderNotFoundException {
        InvoiceDTO result = orderService.getInvoice(orderId);
        return ResponseEntity.ok(result);
    }

    @GetMapping(ApiEndpoints.ORDERS + "/all")
    public ResponseEntity<List<BaseOrderDTO>> getAllOrders(@RequestParam(required = false) OrderStatus status) {
        List<BaseOrderDTO> orders = orderService.getOrdersbyStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/history/{customerId}")
    public ResponseEntity<List<BaseOrderDTO>> getOrderHistory(@PathVariable Integer customerId) {
        List<BaseOrderDTO> history = orderService.getOrderHistory(customerId);
        return ResponseEntity.ok(history);
    }

    @PutMapping(ApiEndpoints.ORDER_BY_ID + "/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long orderId,
                                                  @RequestParam OrderStatus orderStatus) {
        orderService.updateOrderStatus(orderId, orderStatus);
        log.info(LoggerMessages.ORDER_STATUS_CHANGED, orderId, orderStatus);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(ApiEndpoints.ORDER_BY_ID + "/delete")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) throws OrderNotFoundException {
        orderService.deleteOrder(orderId);
        log.info(LoggerMessages.ORDER_DELETED, orderId);
        return ResponseEntity.noContent().build();
    }

    
}
