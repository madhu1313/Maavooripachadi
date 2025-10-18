CREATE TABLE IF NOT EXISTS settlement_batch (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
gateway VARCHAR(16) NOT NULL,
payout_date DATE NOT NULL,
total_amount_paise INT NOT NULL,
count_txns INT NOT NULL,
file_id VARCHAR(64),
checksum VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS settlement_line (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
batch_id BIGINT NOT NULL,
order_no VARCHAR(64) NOT NULL,
gateway_payment_id VARCHAR(64),
amount_paise INT NOT NULL,
fee_paise INT,
tax_paise INT,
status VARCHAR(16),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0,
CONSTRAINT fk_settle_line_batch FOREIGN KEY(batch_id) REFERENCES settlement_batch(id) ON DELETE CASCADE
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

CALL add_index_if_missing('settlement_line', 'ix_settle_order', '(order_no)', '');
CALL add_index_if_missing('settlement_line', 'ix_settle_gateway_payment', '(gateway_payment_id)', '');
DROP PROCEDURE IF EXISTS add_index_if_missing;


CREATE TABLE IF NOT EXISTS settlement_reconcile_file (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
file_id VARCHAR(64),
original_name VARCHAR(255),
storage_url MEDIUMTEXT,
gateway VARCHAR(16),
ingested_by VARCHAR(64),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);
