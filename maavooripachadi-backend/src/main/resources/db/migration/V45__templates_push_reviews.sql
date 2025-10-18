CREATE TABLE IF NOT EXISTS outbound_template (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
code VARCHAR(64) UNIQUE NOT NULL,
channel VARCHAR(8) NOT NULL,
locale VARCHAR(16),
subject VARCHAR(255),
body_html MEDIUMTEXT,
body_text MEDIUMTEXT,
enabled BOOLEAN DEFAULT TRUE,
created_by VARCHAR(128),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS push_token (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
device_id VARCHAR(128) NOT NULL,
token VARCHAR(512) UNIQUE NOT NULL,
platform VARCHAR(16),
enabled BOOLEAN DEFAULT TRUE,
last_seen_at TIMESTAMP NULL,
user_id VARCHAR(128),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS outbound_send_log (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
channel VARCHAR(8) NOT NULL,
template_code VARCHAR(64),
target VARCHAR(255),
status VARCHAR(16),
provider_message_id VARCHAR(128),
error MEDIUMTEXT,
metadata_json MEDIUMTEXT,
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

CALL add_index_if_missing('outbound_send_log', 'ix_outbound_log_target', '(target)', '');
DROP PROCEDURE IF EXISTS add_index_if_missing;
