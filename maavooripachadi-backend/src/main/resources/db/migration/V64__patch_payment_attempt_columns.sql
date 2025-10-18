DROP PROCEDURE IF EXISTS patch_payment_attempt_columns;
DELIMITER $$
CREATE PROCEDURE patch_payment_attempt_columns()
BEGIN
  DECLARE has_amount INT DEFAULT 0;
  DECLARE has_amount_paise INT DEFAULT 0;
  DECLARE has_currency INT DEFAULT 0;
  DECLARE has_gateway_order_id INT DEFAULT 0;
  DECLARE has_gateway_payment_id INT DEFAULT 0;
  DECLARE has_gateway_signature INT DEFAULT 0;
  DECLARE has_meta_json INT DEFAULT 0;
  DECLARE has_created_at INT DEFAULT 0;
  DECLARE has_updated_at INT DEFAULT 0;
  DECLARE has_row_version INT DEFAULT 0;

  SELECT COUNT(*) INTO has_amount
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'payment_attempt'
     AND column_name = 'amount';

  SELECT COUNT(*) INTO has_amount_paise
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'payment_attempt'
     AND column_name = 'amount_paise';

  IF has_amount = 1 AND has_amount_paise = 0 THEN
    SET @sql := 'ALTER TABLE payment_attempt CHANGE COLUMN amount amount_paise INT NOT NULL';
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SET has_amount_paise = 1;
  END IF;

  IF has_amount_paise = 0 THEN
    SET @sql := 'ALTER TABLE payment_attempt ADD COLUMN amount_paise INT NOT NULL DEFAULT 0';
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;

  SELECT COUNT(*) INTO has_currency
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'payment_attempt'
     AND column_name = 'currency';

  IF has_currency = 0 THEN
    SET @sql := 'ALTER TABLE payment_attempt ADD COLUMN currency VARCHAR(8) DEFAULT ''INR'' AFTER amount_paise';
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;

  SET @sql := 'ALTER TABLE payment_attempt MODIFY COLUMN gateway VARCHAR(16) NOT NULL';
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;

  SELECT COUNT(*) INTO has_gateway_order_id
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'payment_attempt'
     AND column_name = 'gateway_order_id';

  IF has_gateway_order_id = 0 THEN
    SET @sql := 'ALTER TABLE payment_attempt ADD COLUMN gateway_order_id VARCHAR(64) NULL AFTER currency';
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;

  SELECT COUNT(*) INTO has_gateway_payment_id
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'payment_attempt'
     AND column_name = 'gateway_payment_id';

  IF has_gateway_payment_id = 0 THEN
    SET @sql := 'ALTER TABLE payment_attempt ADD COLUMN gateway_payment_id VARCHAR(64) NULL AFTER gateway_order_id';
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;

  SELECT COUNT(*) INTO has_gateway_signature
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'payment_attempt'
     AND column_name = 'gateway_signature';

  IF has_gateway_signature = 0 THEN
    SET @sql := 'ALTER TABLE payment_attempt ADD COLUMN gateway_signature VARCHAR(128) NULL AFTER gateway_payment_id';
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;

  SELECT COUNT(*) INTO has_meta_json
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'payment_attempt'
     AND column_name = 'meta_json';

  IF has_meta_json = 0 THEN
    SET @sql := 'ALTER TABLE payment_attempt ADD COLUMN meta_json MEDIUMTEXT NULL AFTER gateway_signature';
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;

  SELECT COUNT(*) INTO has_created_at
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'payment_attempt'
     AND column_name = 'created_at';

  IF has_created_at = 0 THEN
    SET @sql := 'ALTER TABLE payment_attempt ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP';
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;

  SELECT COUNT(*) INTO has_updated_at
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'payment_attempt'
     AND column_name = 'updated_at';

  IF has_updated_at = 0 THEN
    SET @sql := 'ALTER TABLE payment_attempt ADD COLUMN updated_at TIMESTAMP NULL';
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;

  SELECT COUNT(*) INTO has_row_version
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'payment_attempt'
     AND column_name = 'row_version';

  IF has_row_version = 0 THEN
    SET @sql := 'ALTER TABLE payment_attempt ADD COLUMN row_version BIGINT NOT NULL DEFAULT 0';
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;

  SET @sql := 'UPDATE payment_attempt SET currency = ''INR'' WHERE currency IS NULL OR currency = ''''';
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;

  SET @sql := 'UPDATE payment_attempt SET amount_paise = 0 WHERE amount_paise IS NULL';
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;

  SET @sql := 'UPDATE payment_attempt SET row_version = 0 WHERE row_version IS NULL';
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$
DELIMITER ;

CALL patch_payment_attempt_columns();
DROP PROCEDURE IF EXISTS patch_payment_attempt_columns;
