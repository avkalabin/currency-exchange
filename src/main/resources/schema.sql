CREATE TABLE IF NOT EXISTS currencies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(3) NOT NULL UNIQUE,
    sign VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS exchange_rates (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    base_currency_id INTEGER NOT NULL,
    target_currency_id INTEGER NOT NULL,
    rate DECIMAL(10,6) NOT NULL,
    FOREIGN KEY (base_currency_id) REFERENCES currencies(id),
    FOREIGN KEY (target_currency_id) REFERENCES currencies(id),
    UNIQUE(base_currency_id, target_currency_id)
);

INSERT OR IGNORE INTO currencies (id, name, code, sign) VALUES
(1, 'United States dollar', 'USD', '$'),
(2, 'Euro', 'EUR', '€'),
(3, 'Russian Ruble', 'RUB', '₽');

INSERT OR IGNORE INTO exchange_rates (id, base_currency_id, target_currency_id, rate) VALUES
(1, 1, 2, 0.9),
(2, 2, 1, 1.1),
(3, 1, 3, 90.0);