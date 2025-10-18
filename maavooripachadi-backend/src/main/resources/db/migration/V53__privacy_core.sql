CREATE TABLE IF NOT EXISTS privacy_policy (
markdown MEDIUMTEXT NOT NULL,
active BOOLEAN NOT NULL DEFAULT FALSE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS consent_record (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
subject_id VARCHAR(255) NULL,
session_id VARCHAR(255) NULL,
category VARCHAR(32) NOT NULL,
status VARCHAR(16) NOT NULL,
policy_version VARCHAR(64) NULL,
source VARCHAR(64) NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS cookie_preference (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
subject_id VARCHAR(255) NULL,
session_id VARCHAR(255) NULL,
analytics BOOLEAN DEFAULT FALSE,
marketing BOOLEAN DEFAULT FALSE,
personalization BOOLEAN DEFAULT FALSE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS dsr_request (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
type VARCHAR(32) NOT NULL,
status VARCHAR(32) NOT NULL,
subject_id VARCHAR(255) NOT NULL,
details MEDIUMTEXT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS privacy_event (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
subject_id VARCHAR(255) NULL,
kind VARCHAR(64) NOT NULL,
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

CALL add_index_if_missing('consent_record', 'ix_consent_user', '(subject_id)', '');
CALL add_index_if_missing('consent_record', 'ix_consent_session', '(session_id)', '');
CALL add_index_if_missing('cookie_preference', 'ix_cookie_subject', '(subject_id)', '');
CALL add_index_if_missing('dsr_request', 'ix_dsr_subject', '(subject_id)', '');
CALL add_index_if_missing('privacy_event', 'ix_privacy_event', '(subject_id)', '');
DROP PROCEDURE IF EXISTS add_index_if_missing;
