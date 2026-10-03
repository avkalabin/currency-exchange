package dto;

public record CurrencyResponseDto(
        int id,
        String name,
        String code,
        String sign
) {
}
