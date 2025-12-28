package com.hemant.ordermanagement.Services.Impl;

import com.hemant.ordermanagement.Services.MockPaymentGateway;
import com.hemant.ordermanagement.Services.RefundService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RefundServiceImpl implements RefundService {

    private final MockPaymentGateway paymentGateway;

    public RefundServiceImpl(MockPaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    @Override
    public void processRefund(Long orderId, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        paymentGateway.refund(orderId, amount);
    }
}

