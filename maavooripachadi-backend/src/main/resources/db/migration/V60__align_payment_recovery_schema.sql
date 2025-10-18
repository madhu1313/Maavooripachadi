-- Align legacy payment_recovery_token schema with JPA entity expectations.
DROP PROCEDURE IF EXISTS rename_prt_used;
DELIMITER $$
CREATE PROCEDURE rename_prt_used()
BEGIN
  DECLARE consumed_exists INT DEFAULT 0;
  DECLARE used_exists INT DEFAULT 0;
  SELECT COUNT(1)
    INTO consumed_exists
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'payment_recovery_token'
     AND column_name = 'consumed';

  SELECT COUNT(1)
    INTO used_exists
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'payment_recovery_token'
     AND column_name = 'used';

  IF consumed_exists = 0 AND used_exists = 1 THEN
    ALTER TABLE payment_recovery_token
      CHANGE COLUMN used consumed BOOLEAN NOT NULL DEFAULT FALSE;
  END IF;

  IF consumed_exists = 0 AND used_exists = 0 THEN
    ALTER TABLE payment_recovery_token
      ADD COLUMN consumed BOOLEAN NOT NULL DEFAULT FALSE AFTER expires_at;
  END IF;
END$$
DELIMITER ;
CALL rename_prt_used();
DROP PROCEDURE IF EXISTS rename_prt_used;

-- Ensure audit/optimistic locking columns exist.
DROP PROCEDURE IF EXISTS add_column_if_missing;
DELIMITER $$
CREATE PROCEDURE add_column_if_missing(
  IN in_column VARCHAR(128),
  IN in_definition VARCHAR(512)
)
BEGIN
  DECLARE col_exists INT DEFAULT 0;
  SELECT COUNT(1)
    INTO col_exists
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'payment_recovery_token'
     AND column_name = in_column;
  IF col_exists = 0 THEN
    SET @ddl := CONCAT('ALTER TABLE payment_recovery_token ADD COLUMN ', in_column, ' ', in_definition);
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END$$
DELIMITER ;

CALL add_column_if_missing('updated_at', 'TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL add_column_if_missing('row_version', 'BIGINT NOT NULL DEFAULT 0');

DROP PROCEDURE IF EXISTS add_column_if_missing;
