package com.hemant.ordermanagement.Services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MockPaymentGateway {

    public void refund(Long orderId, BigDecimal amount) {
        System.out.println(
                "Refund initiated for orderId=" + orderId +
                        ", amount=" + amount
        );
    }
}

