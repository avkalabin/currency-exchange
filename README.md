# Currency Exchange

Приложение для хранения курсов валют и конвертации денег.

## Стек

Java 21, Jakarta Servlet, SQLite, Maven.

## Запуск

Нужны JDK 21, Maven и Tomcat.

```powershell
mvn clean package
```

Разверните `target/currency-exchange.war` в Tomcat и откройте `http://localhost:8080/currency-exchange/`.

База данных создаётся автоматически при первом запуске. По умолчанию доступны `USD`, `EUR`, `RUB`.

## API

Базовый адрес: `http://localhost:8080/currency-exchange`

| Метод | Путь | Описание |
| --- | --- | --- |
| GET | `/currencies` | Список валют |
| GET | `/currency/USD` | Валюта по коду |
| POST | `/currencies` | Добавить валюту |
| GET | `/exchangeRates` | Список курсов |
| POST | `/exchangeRates` | Добавить курс |
| GET | `/exchangeRate/USDEUR` | Курс валютной пары |
| PATCH | `/exchangeRate/USDEUR` | Изменить курс |
| GET | `/exchange?from=USD&to=RUB&amount=10` | Конвертировать сумму |
