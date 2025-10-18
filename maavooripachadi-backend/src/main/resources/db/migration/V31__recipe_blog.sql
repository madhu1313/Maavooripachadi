CREATE TABLE IF NOT EXISTS recipe (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
slug VARCHAR(128) UNIQUE NOT NULL,
title VARCHAR(255) NOT NULL,
hero_image_url TEXT,
tags VARCHAR(512),
intro_html MEDIUMTEXT,
ingredients_html MEDIUMTEXT,
steps_html MEDIUMTEXT,
published BOOLEAN DEFAULT FALSE,
author VARCHAR(128),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NULL,
row_version BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS blog_post (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
slug VARCHAR(128) UNIQUE NOT NULL,
title VARCHAR(255) NOT NULL,
hero_image_url TEXT,
tags VARCHAR(512),
excerpt_html MEDIUMTEXT,
body_html MEDIUMTEXT,
published BOOLEAN DEFAULT FALSE,
author VARCHAR(128),
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

CALL add_index_if_missing('recipe', 'ix_recipe_title', '(title)', '');
CALL add_index_if_missing('blog_post', 'ix_blog_title', '(title)', '');
DROP PROCEDURE IF EXISTS add_index_if_missing;
