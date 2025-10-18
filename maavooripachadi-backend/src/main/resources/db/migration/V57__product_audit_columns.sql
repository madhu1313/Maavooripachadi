DROP PROCEDURE IF EXISTS add_column_if_missing;
DELIMITER $$
CREATE PROCEDURE add_column_if_missing(
  IN in_table VARCHAR(128),
  IN in_column VARCHAR(128),
  IN in_definition VARCHAR(512)
)
BEGIN
  DECLARE col_exists INT DEFAULT 0;
  SELECT COUNT(1)
    INTO col_exists
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = in_table
     AND column_name = in_column;
  IF col_exists = 0 THEN
    SET @ddl := CONCAT('ALTER TABLE ', in_table, ' ADD COLUMN ', in_column, ' ', in_definition);
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END$$
DELIMITER ;

CALL add_column_if_missing(
  'product',
  'updated_at',
  'TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP'
);

CALL add_column_if_missing(
  'product',
  'row_version',
  'BIGINT NOT NULL DEFAULT 0'
);

DROP PROCEDURE IF EXISTS add_column_if_missing;

UPDATE product
   SET updated_at = IFNULL(updated_at, NOW()),
       row_version = IFNULL(row_version, 0);
