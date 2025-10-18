CREATE TABLE IF NOT EXISTS return_request (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
order_no VARCHAR(64) NOT NULL,
status VARCHAR(32) NOT NULL,
refund_method VARCHAR(32) NOT NULL,
rma_code VARCHAR(64) NULL,
approved_at TIMESTAMP NULL,
received_at TIMESTAMP NULL,
closed_at TIMESTAMP NULL,
customer_email VARCHAR(255) NULL,
notes MEDIUMTEXT NULL,
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


CREATE TABLE IF NOT EXISTS return_item (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
request_id BIGINT NOT NULL,
order_line_id BIGINT NOT NULL,
variant_id BIGINT NOT NULL,
reason VARCHAR(32) NOT NULL,
qty INT NOT NULL,
received_qty INT NOT NULL DEFAULT 0,
refund_paise INT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0,
CONSTRAINT fk_rtn_item_req FOREIGN KEY(request_id) REFERENCES return_request(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS return_event (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
request_id BIGINT NOT NULL,
kind VARCHAR(64) NOT NULL,
payload_json MEDIUMTEXT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0,
CONSTRAINT fk_rtn_ev_req FOREIGN KEY(request_id) REFERENCES return_request(id) ON DELETE CASCADE
);
CALL add_index_if_missing('return_request', 'ix_rtn_order', '(order_no)', '');
CALL add_index_if_missing('return_request', 'ix_rtn_status', '(status)', '');
CALL add_index_if_missing('return_event', 'ix_return_event_req', '(request_id)', '');
DROP PROCEDURE IF EXISTS add_index_if_missing;
