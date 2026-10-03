package dto;

import java.math.BigDecimal;

public record ExchangeRateResponseDto(
        int id,
        CurrencyResponseDto baseCurrency,
        CurrencyResponseDto targetCurrency,
        BigDecimal rate
) {
}
