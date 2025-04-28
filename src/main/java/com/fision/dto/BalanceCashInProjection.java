package com.fision.dto;

import java.math.BigDecimal;

/**
 * @author LordDev
 */
public interface BalanceCashInProjection {
    String getBankName();
    BigDecimal getTotalCashInValue();
    BigDecimal getTotalBalance();
}
