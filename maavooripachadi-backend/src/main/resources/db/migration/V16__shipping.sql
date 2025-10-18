CREATE TABLE IF NOT EXISTS ship_zone (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL UNIQUE,
  pincodes_csv MEDIUMTEXT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS ship_carrier_acct (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  carrier VARCHAR(32) NOT NULL,
  account_id VARCHAR(255) NULL,
  api_key VARCHAR(255) NULL,
  api_secret VARCHAR(255) NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS ship_rate_card (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  carrier VARCHAR(32) NOT NULL,
  service VARCHAR(32) NOT NULL,
  zone_id BIGINT NOT NULL,
  base_paise INT NOT NULL,
  per500g_paise INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_rate_zone FOREIGN KEY(zone_id) REFERENCES ship_zone(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS shipment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(64) NOT NULL,
  status VARCHAR(32) NOT NULL,
  carrier VARCHAR(32) NULL,
  service_level VARCHAR(32) NULL,
  tracking_no VARCHAR(64) NULL,
  label_status VARCHAR(32) NULL,
  label_url MEDIUMTEXT NULL,
  weight_grams INT NOT NULL,
  length_cm INT NOT NULL,
  width_cm INT NOT NULL,
  height_cm INT NOT NULL,
  from_pincode VARCHAR(16) NULL,
  to_pincode VARCHAR(16) NULL,
  to_name VARCHAR(255) NULL,
  to_phone VARCHAR(64) NULL,
  to_address1 VARCHAR(255) NULL,
  to_address2 VARCHAR(255) NULL,
  to_city VARCHAR(128) NULL,
  to_state VARCHAR(128) NULL,
  cod_paise INT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tracking_event (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  shipment_id BIGINT NOT NULL,
  status VARCHAR(64) NULL,
  location VARCHAR(255) NULL,
  details MEDIUMTEXT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_track_ship FOREIGN KEY(shipment_id) REFERENCES shipment(id) ON DELETE CASCADE
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

CALL add_index_if_missing('shipment', 'ix_ship_order', '(order_no)', 'UNIQUE');
CALL add_index_if_missing('tracking_event', 'ix_track_ship', '(shipment_id)', '');
DROP PROCEDURE IF EXISTS add_index_if_missing;
