CREATE TABLE IF NOT EXISTS support_ticket (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  ticket_no VARCHAR(64) NOT NULL UNIQUE,
  subject VARCHAR(255) NOT NULL,
  description MEDIUMTEXT NULL,
  status VARCHAR(32) NOT NULL,
  priority VARCHAR(16) NOT NULL,
  channel VARCHAR(16) NULL,
  requester_email VARCHAR(255) NULL,
  requester_name VARCHAR(255) NULL,
  assignee VARCHAR(255) NULL,
  first_response_due_at TIMESTAMP NULL,
  resolve_due_at TIMESTAMP NULL,
  closed_at TIMESTAMP NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS support_ticket_tag (
  ticket_id BIGINT NOT NULL,
  tag VARCHAR(64) NOT NULL,
  PRIMARY KEY(ticket_id, tag),
  CONSTRAINT fk_tag_ticket FOREIGN KEY(ticket_id) REFERENCES support_ticket(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS support_message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  ticket_id BIGINT NOT NULL,
  author VARCHAR(255) NULL,
  visibility VARCHAR(16) NULL,
  body MEDIUMTEXT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_msg_ticket FOREIGN KEY(ticket_id) REFERENCES support_ticket(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS support_attachment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  ticket_id BIGINT NOT NULL,
  file_name VARCHAR(255) NULL,
  url MEDIUMTEXT NULL,
  size_bytes BIGINT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_attach_ticket FOREIGN KEY(ticket_id) REFERENCES support_ticket(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS support_event (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  ticket_id BIGINT NOT NULL,
  kind VARCHAR(64) NOT NULL,
  payload_json MEDIUMTEXT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_evt_ticket FOREIGN KEY(ticket_id) REFERENCES support_ticket(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS support_sla (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  first_response_mins INT NOT NULL,
  resolve_mins INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS support_canned (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  key_name VARCHAR(128) NOT NULL UNIQUE,
  body MEDIUMTEXT NULL,
  locale VARCHAR(32) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS support_csat (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  ticket_id BIGINT NOT NULL UNIQUE,
  rating INT NOT NULL,
  comment MEDIUMTEXT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_csat_ticket FOREIGN KEY(ticket_id) REFERENCES support_ticket(id) ON DELETE CASCADE
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

CALL add_index_if_missing('support_ticket', 'ix_ticket_status', '(status)', '');
CALL add_index_if_missing('support_ticket', 'ix_ticket_assignee', '(assignee)', '');
CALL add_index_if_missing('support_message', 'ix_msg_ticket', '(ticket_id)', '');
CALL add_index_if_missing('support_attachment', 'ix_attach_ticket', '(ticket_id)', '');
CALL add_index_if_missing('support_event', 'ix_evt_ticket', '(ticket_id)', '');
CALL add_index_if_missing('support_csat', 'ix_csat_ticket', '(ticket_id)', '');
DROP PROCEDURE IF EXISTS add_index_if_missing;
