package mapper;

import dto.ExchangeRateResponseDto;
import model.ExchangeRate;

public final class ExchangeRateMapper {

    private ExchangeRateMapper() {
    }

    public static ExchangeRateResponseDto toDto(ExchangeRate exchangeRate) {
        return new ExchangeRateResponseDto(
                exchangeRate.id(),
                CurrencyMapper.toDto(exchangeRate.baseCurrency()),
                CurrencyMapper.toDto(exchangeRate.targetCurrency()),
                exchangeRate.rate()
        );
    }
}
