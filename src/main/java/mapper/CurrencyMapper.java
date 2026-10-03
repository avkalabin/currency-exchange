package mapper;

import dto.CurrencyResponseDto;
import model.Currency;

public final class CurrencyMapper {

    private CurrencyMapper() {
    }

    public static CurrencyResponseDto toDto(Currency currency) {
        return new CurrencyResponseDto(
                currency.id(),
                currency.name(),
                currency.code(),
                currency.sign()
        );
    }

}
