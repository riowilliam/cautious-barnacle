package com.fision.dto;

import java.math.BigDecimal;

/**
 * @author LordDev
 */
public interface BalanceCashOutProjection {
    String getBankName();
    BigDecimal getTotalCashOutValue();
    BigDecimal getTotalBalance();
}
