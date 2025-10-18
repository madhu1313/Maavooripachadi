CREATE TABLE IF NOT EXISTS payment_attempt (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
order_no VARCHAR(64) NOT NULL,
gateway VARCHAR(16) NOT NULL,
status VARCHAR(16) NOT NULL,
amount_paise INT NOT NULL,
currency VARCHAR(8) DEFAULT 'INR',
gateway_order_id VARCHAR(64),
gateway_payment_id VARCHAR(64),
gateway_signature VARCHAR(128),
meta_json MEDIUMTEXT,
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

CALL add_index_if_missing('payment_attempt', 'ix_pay_order', '(order_no)', '');
CALL add_index_if_missing('payment_attempt', 'ix_pay_status', '(status)', '');
DROP PROCEDURE IF EXISTS add_index_if_missing;


CREATE TABLE IF NOT EXISTS payment_refund (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
attempt_id BIGINT NOT NULL,
amount_paise INT NOT NULL,
status VARCHAR(16) NOT NULL,
gateway_refund_id VARCHAR(64),
reason VARCHAR(255),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0,
CONSTRAINT fk_refund_attempt FOREIGN KEY(attempt_id) REFERENCES payment_attempt(id) ON DELETE CASCADE
);
