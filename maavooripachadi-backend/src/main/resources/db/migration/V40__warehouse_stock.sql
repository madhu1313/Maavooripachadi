CREATE TABLE IF NOT EXISTS warehouse (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
code VARCHAR(32) UNIQUE NOT NULL,
name VARCHAR(128) NOT NULL,
line1 VARCHAR(255),
line2 VARCHAR(255),
city VARCHAR(128),
state VARCHAR(64),
pincode VARCHAR(16),
country VARCHAR(32),
active BOOLEAN DEFAULT TRUE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS inventory (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
warehouse_id BIGINT NOT NULL,
variant_id BIGINT NOT NULL,
on_hand INT NOT NULL,
reserved INT NOT NULL,
reorder_level INT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0,
CONSTRAINT uk_inventory_wh_variant UNIQUE(warehouse_id, variant_id),
CONSTRAINT fk_inv_wh FOREIGN KEY(warehouse_id) REFERENCES warehouse(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS stock_movement (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
inventory_id BIGINT NOT NULL,
type VARCHAR(16) NOT NULL,
quantity INT NOT NULL,
reason VARCHAR(255),
ref VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0,
CONSTRAINT fk_mov_inv FOREIGN KEY(inventory_id) REFERENCES inventory(id) ON DELETE CASCADE
);


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
CONSTRAINT fk_log_ship_wh FOREIGN KEY(warehouse_id) REFERENCES warehouse(id)
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
CONSTRAINT fk_log_pkg_ship FOREIGN KEY(shipment_id) REFERENCES log_shipment(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS carrier_account (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
carrier VARCHAR(32) NOT NULL,
api_key VARCHAR(255),
api_secret VARCHAR(255),
enabled BOOLEAN DEFAULT TRUE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
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

CALL add_index_if_missing('inventory', 'ix_inv_variant', '(variant_id)', '');
CALL add_index_if_missing('log_shipment', 'ix_log_ship_status', '(status)', '');
DROP PROCEDURE IF EXISTS add_index_if_missing;
