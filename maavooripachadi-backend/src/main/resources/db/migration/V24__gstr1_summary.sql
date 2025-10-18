CREATE TABLE IF NOT EXISTS gstr1_summary (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
period VARCHAR(7) UNIQUE NOT NULL, -- YYYY-MM
b2c_count INT,
b2c_taxable_paise INT,
b2c_tax_paise INT,
b2b_count INT,
b2b_taxable_paise INT,
b2b_tax_paise INT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);