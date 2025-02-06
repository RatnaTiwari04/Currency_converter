Database and Table Structure
Database Creation
sql

CREATE DATABASE CurrencyDB;
USE CurrencyDB;
Table: currencies
This table stores available currency codes.

CREATE TABLE currencies (
    CurrencyCode VARCHAR(3) PRIMARY KEY,
    CurrencyName VARCHAR(50) NOT NULL
);

-- Insert common currencies
INSERT INTO currencies (CurrencyCode, CurrencyName) VALUES
('USD', 'United States Dollar'),
('EUR', 'Euro'),
('GBP', 'British Pound Sterling'),
('INR', 'Indian Rupee'),
('CAD', 'Canadian Dollar'),
('AUD', 'Australian Dollar'),
('JPY', 'Japanese Yen');
Table: exchangerates
This table stores exchange rates between different currencies.

CREATE TABLE exchangerates (
    fromCurrency VARCHAR(3),
    toCurrency VARCHAR(3),
    Rate DECIMAL(10, 4) NOT NULL,
    LastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (fromCurrency, toCurrency),
    FOREIGN KEY (fromCurrency) REFERENCES currencies(CurrencyCode),
    FOREIGN KEY (toCurrency) REFERENCES currencies(CurrencyCode)
);

-- Insert some sample exchange rates
INSERT INTO exchangerates (fromCurrency, toCurrency, Rate) VALUES
('USD', 'INR', 83.50),
('INR', 'USD', 0.012),
('EUR', 'USD', 1.08),
('USD', 'EUR', 0.92),
('GBP', 'USD', 1.27),
('USD', 'GBP', 0.79),
('CAD', 'USD', 0.75),
('USD', 'CAD', 1.33),
('JPY', 'USD', 0.0068),
('USD', 'JPY', 146.7);
2. Additional Enhancements
You can periodically update exchange rates using an API (like OpenExchangeRates, Forex API).
