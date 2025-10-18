CREATE TABLE IF NOT EXISTS risk_denylist (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  type VARCHAR(32) NOT NULL,
  value VARCHAR(255) NOT NULL,
  reason VARCHAR(255) NULL,
  source VARCHAR(255) NULL,
  expires_at TIMESTAMP NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT uq_deny_type_value UNIQUE(type, value)
);

CREATE TABLE IF NOT EXISTS risk_velocity (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  key_expr VARCHAR(128) NOT NULL,
  window_seconds INT NOT NULL,
  max_count INT NOT NULL,
  description VARCHAR(255) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT uq_velo_key UNIQUE(key_expr)
);

CREATE TABLE IF NOT EXISTS risk_rule (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL UNIQUE,
  operator VARCHAR(16) NOT NULL,
  left_key VARCHAR(255) NOT NULL,
  right_value VARCHAR(255) NOT NULL,
  score_impact INT NOT NULL,
  priority INT NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS risk_event (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  source VARCHAR(32) NOT NULL,
  subject_id VARCHAR(255) NULL,
  email VARCHAR(255) NULL,
  phone VARCHAR(64) NULL,
  ip VARCHAR(64) NULL,
  device_id VARCHAR(255) NULL,
  order_no VARCHAR(64) NULL,
  amount_paise INT NULL,
  currency VARCHAR(8) NULL,
  payload_json MEDIUMTEXT NULL,
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

CALL add_index_if_missing('risk_event', 'ix_risk_evt_source', '(source)', '');
CALL add_index_if_missing('risk_event', 'ix_risk_evt_ip', '(ip)', '');
CALL add_index_if_missing('risk_event', 'ix_risk_evt_email', '(email)', '');

CREATE TABLE IF NOT EXISTS risk_score (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  score INT NOT NULL,
  decision VARCHAR(16) NOT NULL,
  reasons_json MEDIUMTEXT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_risk_score_evt FOREIGN KEY(event_id) REFERENCES risk_event(id) ON DELETE CASCADE
);
CALL add_index_if_missing('risk_score', 'ix_risk_score_evt', '(event_id)', '');

CREATE TABLE IF NOT EXISTS risk_case (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL UNIQUE,
  status VARCHAR(16) NOT NULL,
  assigned_to VARCHAR(255) NULL,
  notes MEDIUMTEXT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_risk_case_evt FOREIGN KEY(event_id) REFERENCES risk_event(id) ON DELETE CASCADE
);
DROP PROCEDURE IF EXISTS add_index_if_missing;
