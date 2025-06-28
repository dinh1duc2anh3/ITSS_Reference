package com.darian.ecommerce.order;

import com.darian.ecommerce.audit.AuditLogService;
import com.darian.ecommerce.audit.enums.ActionType;
import com.darian.ecommerce.auth.UserService;
import com.darian.ecommerce.auth.enums.UserRole;
import com.darian.ecommerce.auth.entity.User;
import com.darian.ecommerce.cart.CartService;
import com.darian.ecommerce.cart.dto.CartDTO;
import com.darian.ecommerce.order.businesslogic.ordersplitter.OrderSplitter;
import com.darian.ecommerce.order.businesslogic.shippingfee.ShippingFeeCalculatorFactory;
import com.darian.ecommerce.order.dto.BaseOrderDTO;
import com.darian.ecommerce.order.dto.DeliveryInfoDTO;
import com.darian.ecommerce.order.dto.InvoiceDTO;
import com.darian.ecommerce.order.dto.OrderDTO;
import com.darian.ecommerce.order.dto.RushOrderDTO;
import com.darian.ecommerce.order.dto.RushOrderDeliveryInfoDTO;
import com.darian.ecommerce.order.dto.SplitOrderDTO;
import com.darian.ecommerce.order.entity.DeliveryInfo;
import com.darian.ecommerce.order.entity.Order;
import com.darian.ecommerce.order.entity.OrderItem;
import com.darian.ecommerce.order.enums.OrderStatus;
import com.darian.ecommerce.order.exception.OrderNotFoundException;
import com.darian.ecommerce.order.mapper.DeliveryInfoMapper;
import com.darian.ecommerce.order.mapper.OrderMapper;
import com.darian.ecommerce.order.mapper.OrderItemMapper;
import com.darian.ecommerce.product.entity.Product;
import com.darian.ecommerce.payment.enums.PaymentStatus;
import com.darian.ecommerce.shared.constants.ErrorMessages;
import com.darian.ecommerce.shared.constants.LoggerMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final ShippingFeeCalculatorFactory calculatorFactory;
    private final CartService cartService;
    private final UserService userService;
    private final AuditLogService auditLogService;
    private final OrderMapper orderMapper;
    private final DeliveryInfoMapper deliveryInfoMapper;
    private final OrderSplitter orderSplitter;
    private final OrderItemMapper orderItemMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ShippingFeeCalculatorFactory calculatorFactory,
                            CartService cartService,
                            UserService userService,
                            AuditLogService auditLogService,
                            OrderMapper orderMapper,
                            DeliveryInfoMapper deliveryInfoMapper,
                            OrderSplitter orderSplitter,
                            OrderItemMapper orderItemMapper) {
        this.orderRepository = orderRepository;
        this.calculatorFactory = calculatorFactory;
        this.cartService = cartService;
        this.userService = userService;
        this.auditLogService = auditLogService;
        this.orderMapper = orderMapper;
        this.deliveryInfoMapper = deliveryInfoMapper;
        this.orderSplitter = orderSplitter;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(CartDTO cartDTO) {
        if (!checkAvailability(cartDTO)) {
            throw new IllegalStateException(String.format(ErrorMessages.VALIDATION_FAILED, "cart items not available"));
        }

        Order order = new Order();
        User user = userService.getUserById(cartDTO.getUserId());
        order.setUser(user);
        
        List<OrderItem> orderItems = orderItemMapper.fromCartItemDTOListToEntityList(cartDTO.getItems(), order);
        order.setItems(orderItems); 
                
        order.setOrderStatus(OrderStatus.DRAFT);
        order.setCreatedDate(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);

        logger.info(LoggerMessages.ORDER_CREATED_DRAFT, savedOrder.getOrderId());
        auditLogService.logOrderAction(order.getUser().getId(), savedOrder.getOrderId(), UserRole.CUSTOMER, ActionType.ORDER_ACTION);
        return orderMapper.toOrderDTO(savedOrder);
    }

    @Override
    @Transactional
    public SplitOrderDTO placeOrder(OrderDTO orderDTO) {
        if (orderDTO == null || orderDTO.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID is required to place an order.");
        }

        Order existingOrder = findOrderById(orderDTO.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderDTO.getOrderId())));

        if (existingOrder.getOrderStatus() != OrderStatus.DRAFT) {
            throw new IllegalStateException("Order with ID " + existingOrder.getOrderId() + " is not in 'DRAFT' status and cannot be placed.");
        }

        if (!validateDeliveryInfo(orderDTO.getDeliveryInfo())) {
            throw new IllegalArgumentException("Invalid or incomplete delivery information.");
        }

        existingOrder.setDeliveryInfo(deliveryInfoMapper.toEntity(orderDTO.getDeliveryInfo()));

        if (orderDTO.isRushOrder()) {
            if (!isRushDeliverySupported(orderMapper.toBaseOrderDTO(existingOrder))) { 
                existingOrder.setIsRushOrder(false);
                existingOrder.setShippingFee(calculatorFactory.getCalculator(orderDTO).calculateShippingFee(orderDTO));
                existingOrder.setOrderStatus(OrderStatus.PENDING);
                
                Order savedStandardOrder = orderRepository.save(existingOrder);

                logger.info(LoggerMessages.ORDER_PLACED, savedStandardOrder.getOrderId());
                auditLogService.logOrderAction(savedStandardOrder.getUser().getId(), savedStandardOrder.getOrderId(), UserRole.CUSTOMER, ActionType.PLACE_ORDER);

                BaseOrderDTO standardOrderResult = orderMapper.toBaseOrderDTO(savedStandardOrder);
                return new SplitOrderDTO(null, standardOrderResult);
            } else {
                SplitOrderDTO splitResult = orderSplitter.splitOrder(orderMapper.toBaseOrderDTO(existingOrder));

                RushOrderDTO rushOrderResult = null;
                BaseOrderDTO standardOrderResult = null;

                if (splitResult.rushOrder() != null && !splitResult.rushOrder().getItems().isEmpty()) {
                    RushOrderDTO rushOrderDTO = splitResult.rushOrder();

                    Order rushOrder = orderMapper.toEntity(rushOrderDTO, true, existingOrder.getRushDeliveryTime()); // This will use OrderMapper's toEntity which calls orderItemMapper
                    rushOrder.setUser(existingOrder.getUser());
                    rushOrder.setDeliveryInfo(existingOrder.getDeliveryInfo());
                    rushOrder.setShippingFee(calculatorFactory.getCalculator(rushOrderDTO).calculateShippingFee(rushOrderDTO));
                    rushOrder.setOrderStatus(OrderStatus.PENDING);
                    rushOrder.setCreatedDate(LocalDateTime.now());

                    Order savedRushOrder = orderRepository.save(rushOrder);

                    logger.info(LoggerMessages.ORDER_CREATED, savedRushOrder.getOrderId());
                    auditLogService.logOrderAction(savedRushOrder.getUser().getId(), savedRushOrder.getOrderId(), UserRole.CUSTOMER, ActionType.PLACE_ORDER);

                    rushOrderResult = orderMapper.toRushOrderDTO(savedRushOrder);
                }

                if (splitResult.standardOrder() != null && !splitResult.standardOrder().getItems().isEmpty()) {
                    BaseOrderDTO standardOrderDTO = splitResult.standardOrder();
                    existingOrder.setItems(orderItemMapper.toEntityList(standardOrderDTO.getItems(), existingOrder));
                    existingOrder.setIsRushOrder(false);
                    existingOrder.setShippingFee(calculatorFactory.getCalculator(standardOrderDTO).calculateShippingFee(standardOrderDTO));
                    existingOrder.setOrderStatus(OrderStatus.PENDING);

                    Order savedStandardOrder = orderRepository.save(existingOrder);

                    logger.info(LoggerMessages.ORDER_PLACED, savedStandardOrder.getOrderId());
                    auditLogService.logOrderAction(savedStandardOrder.getUser().getId(), savedStandardOrder.getOrderId(), UserRole.CUSTOMER, ActionType.PLACE_ORDER);

                    standardOrderResult = orderMapper.toBaseOrderDTO(savedStandardOrder);
                } else {
                    if (rushOrderResult != null) {
                        logger.info(LoggerMessages.ORDER_DELETED_DRAFT, existingOrder.getOrderId());
                        orderRepository.delete(existingOrder);
                        auditLogService.logOrderAction(existingOrder.getUser().getId(), existingOrder.getOrderId(), UserRole.CUSTOMER, ActionType.ORDER_ACTION);
                    }
                }

                return new SplitOrderDTO(rushOrderResult, standardOrderResult);
            }
        } else {
            existingOrder.setIsRushOrder(false);
            existingOrder.setShippingFee(calculatorFactory.getCalculator(orderDTO).calculateShippingFee(orderDTO));
            existingOrder.setOrderStatus(OrderStatus.PENDING);
            
            Order savedStandardOrder = orderRepository.save(existingOrder);

            logger.info(LoggerMessages.ORDER_PLACED, savedStandardOrder.getOrderId());
            auditLogService.logOrderAction(savedStandardOrder.getUser().getId(), savedStandardOrder.getOrderId(), UserRole.CUSTOMER, ActionType.PLACE_ORDER);

            BaseOrderDTO standardOrderResult = orderMapper.toBaseOrderDTO(savedStandardOrder);
            return new SplitOrderDTO(null, standardOrderResult);
        }
    }

    @Override
    public InvoiceDTO getInvoice(Long orderId) throws OrderNotFoundException {
        Order order = findOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderId)));
        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setOrderId(orderId);
        invoice.setShippingFee(order.getShippingFee());
        invoice.setTotal(order.getTotal());
        return invoice;
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) throws OrderNotFoundException {
        Order order = findOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderId)));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException(String.format(ErrorMessages.ORDER_ALREADY_CANCELLED, orderId));
        }

        if (!checkCancellationValidity(orderId)) {
            throw new IllegalStateException(String.format(ErrorMessages.ORDER_CANNOT_BE_MODIFIED, orderId));
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        logger.info(LoggerMessages.ORDER_STATUS_CHANGED, order.getOrderStatus(), OrderStatus.CANCELLED, orderId);
        auditLogService.logOrderAction(order.getUser().getId(), orderId, UserRole.CUSTOMER, ActionType.CANCEL_ORDER);
    }

    @Override
    public Boolean validateDeliveryInfo(DeliveryInfoDTO deliveryInfoDTO) {
        return deliveryInfoDTO != null
                && deliveryInfoDTO.getRecipientName() != null
                && !deliveryInfoDTO.getRecipientName().isBlank()
                && deliveryInfoDTO.getAddress() != null
                && !deliveryInfoDTO.getAddress().isBlank()
                && deliveryInfoDTO.getProvinceCity() != null
                && !deliveryInfoDTO.getProvinceCity().isBlank();
    }

    @Override
    @Transactional
    public OrderDTO setDeliveryInfo(Long orderId, DeliveryInfoDTO deliveryInfoDTO) {
        if (!validateDeliveryInfo(deliveryInfoDTO)) {
            throw new IllegalArgumentException("Invalid delivery info");
        }
        Order order = findOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderId)));
        
        order.setDeliveryInfo(deliveryInfoMapper.toEntity(deliveryInfoDTO));
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toOrderDTO(updatedOrder);
    }

    @Override
    @Transactional
    public RushOrderDTO setRushDeliveryInfo(Long orderId, RushOrderDeliveryInfoDTO rushOrderUpdateDTO) {
        Order order = findOrderById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderId)));

        if (!Boolean.TRUE.equals(order.getIsRushOrder())) {
            throw new IllegalStateException("Order is not a rush order.");
        }

        if (order.getDeliveryInfo() != null) {
            order.getDeliveryInfo().setDeliveryInstructions(rushOrderUpdateDTO.getDeliveryInstruction());
        }
        order.setRushDeliveryTime(rushOrderUpdateDTO.getRushDeliveryTime());
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toRushOrderDTO(savedOrder);
    }

    @Override
    public Boolean checkAvailability(CartDTO cartDTO) {
        return cartService.checkAvailability(cartDTO);
    }

    public List<BaseOrderDTO> getOrdersbyStatus(OrderStatus status) {
        return orderRepository.findAll().stream()
                .filter(order -> status == null || order.getOrderStatus() == status)
                .map(order -> {
                    if (Boolean.TRUE.equals(order.getIsRushOrder())) {
                        return orderMapper.toRushOrderDTO(order);
                    } else {
                        return orderMapper.toBaseOrderDTO(order);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public Boolean checkCancellationValidity(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderId)));
        return order.getOrderStatus() == OrderStatus.PENDING || order.getOrderStatus() == OrderStatus.DRAFT;
    }

    @Override
    public Boolean isRushDeliverySupported(BaseOrderDTO baseOrderDTO) {
        return baseOrderDTO.getDeliveryInfo().getProvinceCity().equalsIgnoreCase("hanoi")
            && baseOrderDTO.getItems().stream().allMatch(OrderItemDTO::isRushEligible);
    }

    @Override
    public Optional<Order> findOrderById(Long orderId){
        return orderRepository.findById(orderId);
    }

    @Override
    public BaseOrderDTO getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderId)));

        if (Boolean.TRUE.equals(order.getIsRushOrder())) {
            return orderMapper.toRushOrderDTO(order); 
        } else {
            return orderMapper.toBaseOrderDTO(order);
        }
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        orderRepository.updatePaymentStatus(orderId, paymentStatus);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        if (orderStatus == null) {
            throw new IllegalArgumentException("Order status cannot be null");
        }
        if (orderStatus == OrderStatus.DRAFT) {
            throw new IllegalArgumentException("Cannot update order status to DRAFT");
        }
        if (orderStatus == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot update order status to CANCELLED directly. Use cancelOrder method instead.");
        }
        orderRepository.updateOrderStatus(orderId, orderStatus);
    }

    @Override
    public List<BaseOrderDTO> getOrderHistory(Integer customerId) {
        return orderRepository.findByUser_Id(customerId).stream()
                .map(order -> getOrderDetails(order.getOrderId()))
                .collect(Collectors.toList());
    }

}