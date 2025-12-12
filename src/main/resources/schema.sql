PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS Currencies (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Code VARCHAR(3) NOT NULL UNIQUE,
    FUllName VARCHAR(50) NOT NULL,
    Sign VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS ExchangeRates (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    BaseCurrencyId INTEGER NOT NULL,
    TargetCurrencyId INTEGER NOT NULL,
    Rate REAL NOT NULL,
    FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies(ID),
    FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies(ID),
    UNIQUE (BaseCurrencyId, TargetCurrencyId)
);

INSERT INTO Currencies (Code, FUllName, Sign) VALUES
    ('RUB', 'Russian Ruble', '₽'),
    ('USD', 'United States Dollar', '$'),
    ('EUR', 'Euro', '€');

 INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES
  (1, 2, 0.8584),  -- USD to EUR
  (2, 1, 1.17),  -- EUR to USD
  (1, 3, 76.09); -- USD to RUB