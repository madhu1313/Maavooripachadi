DROP PROCEDURE IF EXISTS add_variant_column_if_missing;
DELIMITER $$
CREATE PROCEDURE add_variant_column_if_missing(
  IN in_column VARCHAR(128),
  IN in_definition VARCHAR(512)
)
BEGIN
  DECLARE col_exists INT DEFAULT 0;
  SELECT COUNT(1)
    INTO col_exists
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = 'variant'
     AND column_name = in_column;
  IF col_exists = 0 THEN
    SET @ddl := CONCAT('ALTER TABLE variant ADD COLUMN ', in_column, ' ', in_definition);
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END$$
DELIMITER ;

CALL add_variant_column_if_missing('updated_at', 'TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
CALL add_variant_column_if_missing('row_version', 'BIGINT NOT NULL DEFAULT 0');

DROP PROCEDURE IF EXISTS add_variant_column_if_missing;
