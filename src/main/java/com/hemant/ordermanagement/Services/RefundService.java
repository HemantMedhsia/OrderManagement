package com.hemant.ordermanagement.Services;

import java.math.BigDecimal;

public interface RefundService {
    void processRefund(Long orderId, BigDecimal amount);
}

