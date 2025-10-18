CREATE TABLE IF NOT EXISTS dispute (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
gateway VARCHAR(32) NOT NULL,
provider_case_id VARCHAR(128) NOT NULL,
payment_attempt_id BIGINT,
order_no VARCHAR(64),
status VARCHAR(16) NOT NULL,
reason VARCHAR(64),
type VARCHAR(32),
amount_paise INT NOT NULL,
currency VARCHAR(8) DEFAULT 'INR',
evidence_due_at TIMESTAMP NULL,
decided_at TIMESTAMP NULL,
notes MEDIUMTEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);
CREATE TABLE IF NOT EXISTS dispute_event (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
dispute_id BIGINT NOT NULL,
type VARCHAR(32) NOT NULL,
payload MEDIUMTEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0,
CONSTRAINT fk_dispute_event_dispute FOREIGN KEY(dispute_id) REFERENCES dispute(id) ON DELETE CASCADE
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

CALL add_index_if_missing('dispute', 'uk_dispute_provider', '(provider_case_id)', 'UNIQUE');
CALL add_index_if_missing('dispute', 'ix_dispute_order', '(order_no)', '');
CALL add_index_if_missing('dispute_event', 'ix_dispute_event_dispute', '(dispute_id)', '');
DROP PROCEDURE IF EXISTS add_index_if_missing;
