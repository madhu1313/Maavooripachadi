CREATE TABLE IF NOT EXISTS cart (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
session_id VARCHAR(64) UNIQUE NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS cart_item (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
cart_id BIGINT NOT NULL,
variant_id BIGINT NOT NULL,
qty INT NOT NULL,
unit_price_paise INT NOT NULL,
title VARCHAR(255),
image_url TEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT fk_cart_item_cart FOREIGN KEY(cart_id) REFERENCES cart(id) ON DELETE CASCADE
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

CALL add_index_if_missing('cart_item', 'ix_cart_item_cart', '(cart_id)', '');
CALL add_index_if_missing('cart_item', 'uk_cart_variant', '(cart_id, variant_id)', 'UNIQUE');
DROP PROCEDURE IF EXISTS add_index_if_missing;
