SET @review_table_exists := (
  SELECT COUNT(1)
  FROM information_schema.tables
  WHERE table_schema = DATABASE()
    AND table_name = 'review'
);
SET @review_table_sql := IF(
  @review_table_exists = 0,
  'CREATE TABLE review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    variant_id BIGINT NULL,
    rating INT NOT NULL,
    title VARCHAR(120) NULL,
    body MEDIUMTEXT NULL,
    subject_id VARCHAR(128) NULL,
    verified_purchase BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(16) NOT NULL DEFAULT ''PENDING'',
    helpful_count INT NOT NULL DEFAULT 0,
    not_helpful_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    row_version BIGINT NOT NULL DEFAULT 0
  )',
  'SELECT 1'
);
PREPARE review_table_stmt FROM @review_table_sql;
EXECUTE review_table_stmt;
DEALLOCATE PREPARE review_table_stmt;

SET @review_image_table_exists := (
  SELECT COUNT(1)
  FROM information_schema.tables
  WHERE table_schema = DATABASE()
    AND table_name = 'review_image'
);
SET @review_image_table_sql := IF(
  @review_image_table_exists = 0,
  'CREATE TABLE review_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    review_id BIGINT NOT NULL,
    url MEDIUMTEXT NULL,
    alt_text VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    row_version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_review_image_review_ref FOREIGN KEY(review_id) REFERENCES review(id) ON DELETE CASCADE
  )',
  'SELECT 1'
);
PREPARE review_image_table_stmt FROM @review_image_table_sql;
EXECUTE review_image_table_stmt;
DEALLOCATE PREPARE review_image_table_stmt;

SET @review_vote_table_exists := (
  SELECT COUNT(1)
  FROM information_schema.tables
  WHERE table_schema = DATABASE()
    AND table_name = 'review_vote'
);
SET @review_vote_table_sql := IF(
  @review_vote_table_exists = 0,
  'CREATE TABLE review_vote (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    review_id BIGINT NOT NULL,
    type VARCHAR(16) NOT NULL,
    subject_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    row_version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_review_vote_review_ref FOREIGN KEY(review_id) REFERENCES review(id) ON DELETE CASCADE,
    CONSTRAINT uq_review_vote UNIQUE(review_id, subject_id)
  )',
  'SELECT 1'
);
PREPARE review_vote_table_stmt FROM @review_vote_table_sql;
EXECUTE review_vote_table_stmt;
DEALLOCATE PREPARE review_vote_table_stmt;

SET @review_flag_table_exists := (
  SELECT COUNT(1)
  FROM information_schema.tables
  WHERE table_schema = DATABASE()
    AND table_name = 'review_flag'
);
SET @review_flag_table_sql := IF(
  @review_flag_table_exists = 0,
  'CREATE TABLE review_flag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    review_id BIGINT NOT NULL,
    subject_id VARCHAR(255) NULL,
    reason VARCHAR(64) NULL,
    details MEDIUMTEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    row_version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_review_flag_review_ref FOREIGN KEY(review_id) REFERENCES review(id) ON DELETE CASCADE
  )',
  'SELECT 1'
);
PREPARE review_flag_table_stmt FROM @review_flag_table_sql;
EXECUTE review_flag_table_stmt;
DEALLOCATE PREPARE review_flag_table_stmt;

SET @review_reply_table_exists := (
  SELECT COUNT(1)
  FROM information_schema.tables
  WHERE table_schema = DATABASE()
    AND table_name = 'review_reply'
);
SET @review_reply_table_sql := IF(
  @review_reply_table_exists = 0,
  'CREATE TABLE review_reply (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    review_id BIGINT NOT NULL,
    author VARCHAR(255) NOT NULL,
    body MEDIUMTEXT NULL,
    public_visible BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    row_version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_review_reply_review_ref FOREIGN KEY(review_id) REFERENCES review(id) ON DELETE CASCADE
  )',
  'SELECT 1'
);
PREPARE review_reply_table_stmt FROM @review_reply_table_sql;
EXECUTE review_reply_table_stmt;
DEALLOCATE PREPARE review_reply_table_stmt;

SET @rating_agg_table_exists := (
  SELECT COUNT(1)
  FROM information_schema.tables
  WHERE table_schema = DATABASE()
    AND table_name = 'product_rating_agg'
);
SET @rating_agg_table_sql := IF(
  @rating_agg_table_exists = 0,
  'CREATE TABLE product_rating_agg (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    variant_id BIGINT NULL,
    count_reviews INT NOT NULL DEFAULT 0,
    avg_rating DOUBLE NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    row_version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_rating_product_variant UNIQUE(product_id, variant_id)
  )',
  'SELECT 1'
);
PREPARE rating_agg_table_stmt FROM @rating_agg_table_sql;
EXECUTE rating_agg_table_stmt;
DEALLOCATE PREPARE rating_agg_table_stmt;


DROP PROCEDURE IF EXISTS add_column_if_missing;
DELIMITER $$
CREATE PROCEDURE add_column_if_missing(
  IN in_table VARCHAR(128),
  IN in_column VARCHAR(128),
  IN in_definition TEXT
)
BEGIN
  DECLARE col_exists INT DEFAULT 0;
  SELECT COUNT(1)
    INTO col_exists
    FROM information_schema.columns
   WHERE table_schema = DATABASE()
     AND table_name = in_table
     AND column_name = REPLACE(in_column, '`', '');
  IF col_exists = 0 THEN
    SET @ddl := CONCAT('ALTER TABLE ', in_table, ' ADD COLUMN ', in_column, ' ', in_definition);
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END$$
DELIMITER ;

CALL add_column_if_missing('review', '`product_id`', 'BIGINT NOT NULL');
CALL add_column_if_missing('review', '`variant_id`', 'BIGINT NULL');
CALL add_column_if_missing('review', '`rating`', 'INT NOT NULL DEFAULT 0');
CALL add_column_if_missing('review', '`title`', 'VARCHAR(120) NULL');
CALL add_column_if_missing('review', '`body`', 'MEDIUMTEXT NULL');
CALL add_column_if_missing('review', '`subject_id`', 'VARCHAR(128) NULL');
CALL add_column_if_missing('review', '`verified_purchase`', 'BOOLEAN NOT NULL DEFAULT FALSE');
CALL add_column_if_missing('review', '`status`', 'VARCHAR(16) NOT NULL DEFAULT \'PENDING\'');
CALL add_column_if_missing('review', '`helpful_count`', 'INT NOT NULL DEFAULT 0');
CALL add_column_if_missing('review', '`not_helpful_count`', 'INT NOT NULL DEFAULT 0');
CALL add_column_if_missing('review', '`created_at`', 'TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP');
CALL add_column_if_missing('review', '`updated_at`', 'TIMESTAMP NULL');
CALL add_column_if_missing('review', '`row_version`', 'BIGINT NOT NULL DEFAULT 0');

CALL add_column_if_missing('review_image', '`created_at`', 'TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP');
CALL add_column_if_missing('review_image', '`updated_at`', 'TIMESTAMP NULL');
CALL add_column_if_missing('review_image', '`row_version`', 'BIGINT NOT NULL DEFAULT 0');

CALL add_column_if_missing('review_vote', '`created_at`', 'TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP');
CALL add_column_if_missing('review_vote', '`updated_at`', 'TIMESTAMP NULL');
CALL add_column_if_missing('review_vote', '`row_version`', 'BIGINT NOT NULL DEFAULT 0');

CALL add_column_if_missing('review_flag', '`created_at`', 'TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP');
CALL add_column_if_missing('review_flag', '`updated_at`', 'TIMESTAMP NULL');
CALL add_column_if_missing('review_flag', '`row_version`', 'BIGINT NOT NULL DEFAULT 0');

CALL add_column_if_missing('review_reply', '`created_at`', 'TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP');
CALL add_column_if_missing('review_reply', '`updated_at`', 'TIMESTAMP NULL');
CALL add_column_if_missing('review_reply', '`row_version`', 'BIGINT NOT NULL DEFAULT 0');

CALL add_column_if_missing('product_rating_agg', '`created_at`', 'TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP');
CALL add_column_if_missing('product_rating_agg', '`updated_at`', 'TIMESTAMP NULL');
CALL add_column_if_missing('product_rating_agg', '`row_version`', 'BIGINT NOT NULL DEFAULT 0');

DROP PROCEDURE IF EXISTS add_column_if_missing;

DROP PROCEDURE IF EXISTS drop_foreign_key_if_exists;
DELIMITER $$
CREATE PROCEDURE drop_foreign_key_if_exists(
  IN in_table VARCHAR(128),
  IN in_constraint VARCHAR(128)
)
BEGIN
  DECLARE fk_exists INT DEFAULT 0;
  SELECT COUNT(1)
    INTO fk_exists
    FROM information_schema.REFERENTIAL_CONSTRAINTS
   WHERE constraint_schema = DATABASE()
     AND constraint_name = in_constraint
     AND table_name = in_table;
  IF fk_exists > 0 THEN
    SET @ddl := CONCAT('ALTER TABLE ', in_table, ' DROP FOREIGN KEY ', in_constraint);
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END$$
DELIMITER ;

CALL drop_foreign_key_if_exists('review_image', 'fk_review_image_review');
CALL drop_foreign_key_if_exists('review_vote', 'fk_review_vote_review');
CALL drop_foreign_key_if_exists('review_flag', 'fk_review_flag_review');
CALL drop_foreign_key_if_exists('review_reply', 'fk_review_reply_review');

DROP PROCEDURE IF EXISTS drop_foreign_key_if_exists;

DROP PROCEDURE IF EXISTS add_foreign_key_if_missing;
DELIMITER $$
CREATE PROCEDURE add_foreign_key_if_missing(
  IN in_table VARCHAR(128),
  IN in_constraint VARCHAR(128),
  IN in_definition TEXT
)
BEGIN
  DECLARE fk_exists INT DEFAULT 0;
  SELECT COUNT(1)
    INTO fk_exists
    FROM information_schema.REFERENTIAL_CONSTRAINTS
   WHERE constraint_schema = DATABASE()
     AND constraint_name = in_constraint
     AND table_name = in_table;
  IF fk_exists = 0 THEN
    SET @ddl := CONCAT('ALTER TABLE ', in_table, ' ADD CONSTRAINT ', in_constraint, ' ', in_definition);
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END$$
DELIMITER ;

CALL add_foreign_key_if_missing('review_image', 'fk_review_image_review_ref', 'FOREIGN KEY(review_id) REFERENCES review(id) ON DELETE CASCADE');
CALL add_foreign_key_if_missing('review_vote', 'fk_review_vote_review_ref', 'FOREIGN KEY(review_id) REFERENCES review(id) ON DELETE CASCADE');
CALL add_foreign_key_if_missing('review_flag', 'fk_review_flag_review_ref', 'FOREIGN KEY(review_id) REFERENCES review(id) ON DELETE CASCADE');
CALL add_foreign_key_if_missing('review_reply', 'fk_review_reply_review_ref', 'FOREIGN KEY(review_id) REFERENCES review(id) ON DELETE CASCADE');
DROP PROCEDURE IF EXISTS add_foreign_key_if_missing;


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

CALL add_index_if_missing('review', 'ix_review_product', '(product_id)', '');
CALL add_index_if_missing('review', 'ix_review_variant', '(variant_id)', '');
CALL add_index_if_missing('review', 'ix_review_status', '(status)', '');
CALL add_index_if_missing('review_image', 'ix_review_image_review', '(review_id)', '');
CALL add_index_if_missing('review_reply', 'ix_review_reply_review', '(review_id)', '');
CALL add_index_if_missing('review_vote', 'uq_review_vote', '(review_id, subject_id)', 'UNIQUE');
CALL add_index_if_missing('product_rating_agg', 'uq_rating_product_variant', '(product_id, variant_id)', 'UNIQUE');
DROP PROCEDURE IF EXISTS add_index_if_missing;
