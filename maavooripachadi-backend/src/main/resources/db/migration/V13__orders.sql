CREATE TABLE IF NOT EXISTS orders (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
order_no VARCHAR(64) UNIQUE NOT NULL,
status VARCHAR(16) NOT NULL,
payment_status VARCHAR(16) NOT NULL,
subtotal_paise INT NOT NULL,
shipping_paise INT NOT NULL,
discount_paise INT NOT NULL,
tax_paise INT NOT NULL,
total_paise INT NOT NULL,
currency VARCHAR(8) DEFAULT 'INR',
customer_email VARCHAR(255),
customer_phone VARCHAR(32),
customer_name VARCHAR(128),
ship_name VARCHAR(128),
ship_phone VARCHAR(32),
ship_line1 VARCHAR(255),
ship_line2 VARCHAR(255),
ship_city VARCHAR(128),
ship_state VARCHAR(64),
ship_pincode VARCHAR(16),
ship_country VARCHAR(32),
notes MEDIUMTEXT,
coupon_code VARCHAR(64),
payment_gateway VARCHAR(32),
payment_ref VARCHAR(128),
paid_at TIMESTAMP NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS order_item (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
order_id BIGINT NOT NULL,
variant_id BIGINT NOT NULL,
sku VARCHAR(64),
title VARCHAR(255),
qty INT NOT NULL,
unit_price_paise INT NOT NULL,
line_total_paise INT NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0,
CONSTRAINT fk_item_order FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS order_note (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
order_id BIGINT NOT NULL,
note MEDIUMTEXT,
author VARCHAR(128),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0,
CONSTRAINT fk_note_order FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE
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

CALL add_index_if_missing('orders', 'ix_order_status', '(status)', '');
DROP PROCEDURE IF EXISTS add_index_if_missing;
