CREATE TABLE IF NOT EXISTS payment_recovery_token (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
token VARCHAR(64) UNIQUE NOT NULL,
order_no VARCHAR(64) NOT NULL,
expires_at TIMESTAMP NULL,
consumed BOOLEAN DEFAULT FALSE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);
DROP PROCEDURE IF EXISTS add_prt_order_column;
DELIMITER $$
CREATE PROCEDURE add_prt_order_column()
BEGIN
  DECLARE col_exists INT DEFAULT 0;
  SELECT COUNT(1)
    INTO col_exists
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'payment_recovery_token'
     AND column_name = 'order_no';
  IF col_exists = 0 THEN
    ALTER TABLE payment_recovery_token
      ADD COLUMN order_no VARCHAR(64) NOT NULL DEFAULT '' AFTER token;
  END IF;
END$$
DELIMITER ;
CALL add_prt_order_column();
DROP PROCEDURE IF EXISTS add_prt_order_column;
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

CALL add_index_if_missing('payment_recovery_token', 'ix_recovery_order', '(order_no)', '');
CALL add_index_if_missing('payment_recovery_token', 'ix_recovery_token', '(token)', '');
DROP PROCEDURE IF EXISTS add_index_if_missing;
