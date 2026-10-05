package dao;

import model.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyDao {

    List<Currency> findAll();

    Currency create(String name, String code, String sign);

    Optional<Currency> findByCode(String code);

}
