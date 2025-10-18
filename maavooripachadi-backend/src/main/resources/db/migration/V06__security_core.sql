CREATE TABLE IF NOT EXISTS sec_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL UNIQUE,
  description VARCHAR(255) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sec_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL UNIQUE,
  description VARCHAR(255) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sec_role_permission (
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, permission_id),
  CONSTRAINT fk_rp_role FOREIGN KEY(role_id) REFERENCES sec_role(id) ON DELETE CASCADE,
  CONSTRAINT fk_rp_perm FOREIGN KEY(permission_id) REFERENCES sec_permission(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sec_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(255) NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sec_user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_ur_user FOREIGN KEY(user_id) REFERENCES sec_user(id) ON DELETE CASCADE,
  CONSTRAINT fk_ur_role FOREIGN KEY(role_id) REFERENCES sec_role(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sec_jwt_token (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  type VARCHAR(16) NOT NULL,
  token VARCHAR(512) NOT NULL UNIQUE,
  revoked BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_token_user FOREIGN KEY(user_id) REFERENCES sec_user(id) ON DELETE CASCADE
);
SET @sec_token_idx_exists := (
  SELECT COUNT(1)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'sec_jwt_token'
    AND index_name = 'ix_token_user'
);
SET @sec_token_idx_sql := IF(
  @sec_token_idx_exists = 0,
  'CREATE INDEX ix_token_user ON sec_jwt_token(user_id)',
  'SELECT 1'
);
PREPARE sec_token_idx_stmt FROM @sec_token_idx_sql;
EXECUTE sec_token_idx_stmt;
DEALLOCATE PREPARE sec_token_idx_stmt;

CREATE TABLE IF NOT EXISTS sec_audit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  actor VARCHAR(255) NULL,
  action VARCHAR(64) NOT NULL,
  details_json MEDIUMTEXT NULL,
  ip VARCHAR(64) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  row_version BIGINT NOT NULL DEFAULT 0
);
SET @sec_audit_idx_exists := (
  SELECT COUNT(1)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'sec_audit_log'
    AND index_name = 'ix_audit_actor'
);
SET @sec_audit_idx_sql := IF(
  @sec_audit_idx_exists = 0,
  'CREATE INDEX ix_audit_actor ON sec_audit_log(actor)',
  'SELECT 1'
);
PREPARE sec_audit_idx_stmt FROM @sec_audit_idx_sql;
EXECUTE sec_audit_idx_stmt;
DEALLOCATE PREPARE sec_audit_idx_stmt;
