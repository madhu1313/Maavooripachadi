CREATE TABLE IF NOT EXISTS product (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
slug VARCHAR(128) UNIQUE NOT NULL,
title VARCHAR(255) NOT NULL,
description_html MEDIUMTEXT,
hero_image_url TEXT,
price_paise INT NOT NULL,
mrp_paise INT,
category_slug VARCHAR(64),
tags VARCHAR(512),
search_text MEDIUMTEXT,
in_stock BOOLEAN DEFAULT TRUE,
badge VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS variant (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
product_id BIGINT NOT NULL,
sku VARCHAR(64) UNIQUE NOT NULL,
label VARCHAR(64),
price_paise INT NOT NULL,
in_stock BOOLEAN DEFAULT TRUE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT fk_variant_product FOREIGN KEY(product_id) REFERENCES product(id) ON DELETE CASCADE
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

CALL add_index_if_missing('product', 'ix_product_category', '(category_slug)', '');
CALL add_index_if_missing('product', 'ix_product_title', '(title)', '');
CALL add_index_if_missing('product', 'ix_product_price', '(price_paise)', '');
DROP PROCEDURE IF EXISTS add_index_if_missing;
