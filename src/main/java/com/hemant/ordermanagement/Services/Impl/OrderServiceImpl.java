package com.hemant.ordermanagement.Services.Impl;

import com.hemant.ordermanagement.Constants.OrderStatus;
import com.hemant.ordermanagement.Exceptions.OrderCancellationException;
import com.hemant.ordermanagement.Models.Order;
import com.hemant.ordermanagement.Repos.OrderRepository;
import com.hemant.ordermanagement.Services.OrderService;
import com.hemant.ordermanagement.Services.RefundService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RefundService refundService;

    public OrderServiceImpl(OrderRepository orderRepository, RefundService refundService) {
        this.orderRepository = orderRepository;
        this.refundService = refundService;
    }

    @Override
    public void cancelOrder(Long orderId, Long userId) {

        Order order = orderRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() ->
                        new OrderCancellationException(
                                "Order with orderId=" + orderId + " and userId=" + userId + " not found"
                        ));

        OrderStatus status = order.getStatus();

        if (status == OrderStatus.CANCELLED) {
            throw new OrderCancellationException("Order already cancelled");
        }

        if (status == OrderStatus.DELIVERED) {
            throw new OrderCancellationException("Delivered order cannot be cancelled");
        }

        if (status == OrderStatus.PACKED) {
            throw new OrderCancellationException("Order already packed, cannot cancel");
        }

        BigDecimal refundAmount = BigDecimal.ZERO;

        if (status == OrderStatus.CREATED) {
            refundAmount = BigDecimal.ZERO;
        }

        else if (status == OrderStatus.PAID) {

            long minutes = Duration.between(
                    order.getPaidAt(),
                    LocalDateTime.now()
            ).toMinutes();

            if (minutes > 30) {
                throw new OrderCancellationException("Cancellation window expired");
            }

            refundAmount = BigDecimal.valueOf(order.getAmount());
        }

        else if (status == OrderStatus.SHIPPED) {

            refundAmount = BigDecimal.valueOf(order.getAmount())
                    .multiply(BigDecimal.valueOf(0.5));
        }

        // 🔁 Refund execution (future integration point)
        // refundService.processRefund(order.getOrderId(), refundAmount);
        refundService.processRefund(order.getOrderId(), refundAmount);


        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
    }
}
