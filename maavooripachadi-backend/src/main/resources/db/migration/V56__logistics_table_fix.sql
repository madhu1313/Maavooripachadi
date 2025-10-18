CREATE TABLE IF NOT EXISTS log_shipment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(64) NOT NULL,
  shipment_no VARCHAR(64) UNIQUE NOT NULL,
  warehouse_id BIGINT,
  carrier VARCHAR(32),
  tracking_no VARCHAR(64),
  status VARCHAR(16) NOT NULL,
  weight_grams INT,
  length_cm INT,
  width_cm INT,
  height_cm INT,
  label_url TEXT,
  manifest_id VARCHAR(64),
  consignee_name VARCHAR(128),
  consignee_phone VARCHAR(32),
  ship_line1 VARCHAR(255),
  ship_line2 VARCHAR(255),
  ship_city VARCHAR(128),
  ship_state VARCHAR(64),
  ship_pincode VARCHAR(16),
  ship_country VARCHAR(32),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_log_ship_wh_migrate FOREIGN KEY(warehouse_id) REFERENCES warehouse(id)
);

CREATE TABLE IF NOT EXISTS log_package_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  shipment_id BIGINT NOT NULL,
  variant_id BIGINT NOT NULL,
  sku VARCHAR(64),
  qty INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_log_pkg_ship_migrate FOREIGN KEY(shipment_id) REFERENCES log_shipment(id) ON DELETE CASCADE
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

CALL add_index_if_missing('log_shipment', 'ix_log_ship_status', '(status)', '');
CALL add_index_if_missing('log_package_item', 'ix_log_pkg_ship', '(shipment_id)', '');

DROP PROCEDURE IF EXISTS add_index_if_missing;

DROP TABLE IF EXISTS package_item;
