ALTER TABLE sec_user
    MODIFY email VARCHAR(255) NULL;

ALTER TABLE sec_user
    ADD COLUMN phone VARCHAR(32) NULL AFTER email;

SET @sec_user_phone_idx_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'sec_user'
      AND index_name = 'ux_sec_user_phone'
);

SET @sec_user_phone_idx_sql := IF(
    @sec_user_phone_idx_exists = 0,
    'CREATE UNIQUE INDEX ux_sec_user_phone ON sec_user(phone)',
    'SELECT 1'
);
PREPARE sec_user_phone_idx_stmt FROM @sec_user_phone_idx_sql;
EXECUTE sec_user_phone_idx_stmt;
DEALLOCATE PREPARE sec_user_phone_idx_stmt;
