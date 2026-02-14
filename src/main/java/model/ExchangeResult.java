package model;

public record ExchangeResult(
        Currency baseCurrency,
        Currency targetCurrency,
        double rate,
        double amount,
        double convertedAmount
) {
}
