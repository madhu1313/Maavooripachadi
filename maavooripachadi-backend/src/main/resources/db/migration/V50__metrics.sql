CREATE TABLE IF NOT EXISTS metric_event (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(64) NOT NULL,
unit VARCHAR(16),
value DOUBLE NOT NULL,
tags_json MEDIUMTEXT,
occurred_at TIMESTAMP NOT NULL,
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


CREATE TABLE IF NOT EXISTS metric_counter (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(64) NOT NULL,
granularity VARCHAR(8) NOT NULL,
window_start TIMESTAMP NOT NULL,
window_end TIMESTAMP NOT NULL,
count BIGINT NOT NULL,
sum DOUBLE,
min_val DOUBLE,
max_val DOUBLE,
key_hash VARCHAR(64),
tags_json MEDIUMTEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);
CREATE TABLE IF NOT EXISTS api_metric (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
method VARCHAR(8),
path VARCHAR(255),
status INT,
duration_ms BIGINT,
user_id VARCHAR(64),
ip VARCHAR(64),
occurred_at TIMESTAMP NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);
CALL add_index_if_missing('metric_event', 'ix_me_name_time', '(name, occurred_at)', '');
CALL add_index_if_missing('metric_counter', 'ix_mc_name_window', '(name, window_start, granularity)', '');
CALL add_index_if_missing('api_metric', 'ix_am_time', '(occurred_at)', '');
CALL add_index_if_missing('api_metric', 'ix_am_path', '(path)', '');
CALL add_index_if_missing('api_metric', 'ix_am_status', '(status)', '');
DROP PROCEDURE IF EXISTS add_index_if_missing;
