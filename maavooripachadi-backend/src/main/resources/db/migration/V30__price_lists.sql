CREATE TABLE IF NOT EXISTS price_list (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(64) NOT NULL,
currency VARCHAR(8) NOT NULL DEFAULT 'INR',
active BOOLEAN NOT NULL DEFAULT TRUE,
is_default BOOLEAN NOT NULL DEFAULT FALSE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS price_list_item (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
price_list_id BIGINT NOT NULL,
variant_id BIGINT NOT NULL,
price_paise INT NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0,
CONSTRAINT fk_pli_pl FOREIGN KEY(price_list_id) REFERENCES price_list(id) ON DELETE CASCADE,
CONSTRAINT uq_pl_variant UNIQUE(price_list_id, variant_id)
);


CREATE TABLE IF NOT EXISTS coupon (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
code VARCHAR(64) NOT NULL UNIQUE,
type VARCHAR(16) NOT NULL,
value INT NOT NULL,
max_discount_paise INT NULL,
min_subtotal_paise INT NULL,
starts_at TIMESTAMP NULL,
ends_at TIMESTAMP NULL,
active BOOLEAN NOT NULL DEFAULT TRUE,
usage_limit INT NULL,
usage_count INT NOT NULL DEFAULT 0,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS tax_rule (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
region_pattern VARCHAR(64) NOT NULL,
rate_percent INT NOT NULL,
active BOOLEAN NOT NULL DEFAULT TRUE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS shipping_rule (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
pincode_pattern VARCHAR(64) NOT NULL,
min_subtotal_paise INT NOT NULL,
fee_paise INT NOT NULL,
active BOOLEAN NOT NULL DEFAULT TRUE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS currency_rate (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
from_ccy VARCHAR(8) NOT NULL,
to_ccy VARCHAR(8) NOT NULL,
rate DOUBLE NOT NULL,
updated_at TIMESTAMP NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at_row TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0,
CONSTRAINT uq_fx_pair UNIQUE(from_ccy, to_ccy)
);


DROP PROCEDURE IF EXISTS add_index_if_missing;
DELIMITER $$
CREATE PROCEDURE add_index_if_missing(
  IN in_table VARCHAR(128),
  IN in_index VARCHAR(128),
  IN in_columns VARCHAR(512),
  IN in_type VARCHAR(16)
)
BEGIN
  DECLARE idx_exists INT DEFAULT 0;
  DECLARE prefix VARCHAR(8) DEFAULT '';
  SELECT COUNT(1)
    INTO idx_exists
    FROM information_schema.statistics
   WHERE table_schema = DATABASE()
     AND table_name = in_table
     AND index_name = in_index;
  IF idx_exists = 0 THEN
    IF in_type IS NOT NULL AND LENGTH(TRIM(in_type)) > 0 THEN
      SET prefix = CONCAT(TRIM(in_type), ' ');
    END IF;
    SET @ddl := CONCAT('CREATE ', prefix, 'INDEX ', in_index, ' ON ', in_table, ' ', in_columns);
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END$$
DELIMITER ;

CALL add_index_if_missing('price_list', 'ix_pl_name', '(name)', 'UNIQUE');
DROP PROCEDURE IF EXISTS add_index_if_missing;
